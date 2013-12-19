package com.ifeng.vshare.requestor;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.net.Uri;

import com.ifeng.util.Utility;
import com.ifeng.util.logging.Log;
import com.ifeng.util.net.parser.AbstractIFItem;
import com.ifeng.util.net.requestor.AbstractRequestor;
import com.ifeng.util.net.requestor.RequestDataCache;
import com.ifeng.vshare.model.BaseVShareItem;

/**
 * 客户端请求的base类，包含了对接口认证的实现，需要认证的数据接口可以继承自此类。注意：本类中已经实现了数据缓存，若需要进行数据的及时查询，
 * 需要自行修改缓存配置 {@link RequestDataCache}
 * 
 * @author Calvin
 * 
 */
public abstract class BaseVShareRequestor extends AbstractRequestor {
	/** key */
	private static final String KEY = "gotone2013";

	/** 上一次结束的请求结果返回 */
	private VShareRequestResult mLastResult = VShareRequestResult.NORESULT;
	/** 上一结束的请求的结果消息 */
	private String mLastResultMessage;

	/**
	 * 构造
	 * 
	 * @param context
	 * @param listener
	 */
	public BaseVShareRequestor(Context context, OnModelProcessListener listener) {
		super(context, listener);
		// 设置requestor缓存
		mRequestDataCache = new RequestDataCache(mContext, KEY);
	}

	@Override
	public void request() {
		mLastResult = VShareRequestResult.NORESULT;
		mLastResultMessage = null;
		super.request();
	}

	@Override
	protected final List<NameValuePair> getExtraParams() {
		List<NameValuePair> extraList = new LinkedList<NameValuePair>();

		extraList.add(new BasicNameValuePair("oauth_signature",
				getOauthSignatureKey()));

		return extraList;
	}

	/**
	 * 获取OauthSignatureKey,MD5(LowerCase(KEY+&+请求类型+&+encode(url)+&+encode(
	 * params.key+ '=' +param.value))).toLowerCase
	 * 
	 * @return
	 */
	private String getOauthSignatureKey() {
		StringBuffer oauthSignature = new StringBuffer();
		oauthSignature.append(KEY);
		oauthSignature.append("&");
		oauthSignature.append(getRequestType().name());
		oauthSignature.append("&");
		oauthSignature.append(Uri.encode(getRequestUrl()));

		List<NameValuePair> params = getRequestParams();
		if (params != null && params.size() > 0) {
			oauthSignature.append("&");

			Comparator<NameValuePair> comparator = new Comparator<NameValuePair>() {
				@Override
				public int compare(NameValuePair lhs, NameValuePair rhs) {
					return lhs.getName().compareTo(rhs.getName());
				}
			};
			Collections.sort(params, comparator);

			StringBuffer paramBuffer = new StringBuffer();
			for (int i = 0; i < params.size(); i++) {
				NameValuePair pair = params.get(i);
				paramBuffer.append(pair.getName());
				paramBuffer.append("=");
				paramBuffer.append(pair.getValue());
				if (i != params.size() - 1) {
					paramBuffer.append("&");
				}
			}

			try {
				oauthSignature.append(URLEncoder.encode(paramBuffer.toString(),
						HTTP.UTF_8));
			} catch (UnsupportedEncodingException e) {
				if (DEBUG) {
					Log.w(TAG, e);
				}
			}
		}

		return Utility.getMD5(oauthSignature.toString().toLowerCase(Locale.US))
				.toLowerCase(Locale.US);
	}

	@Override
	protected final void handleResult(AbstractIFItem item) {
		if (!(item instanceof BaseVShareItem)) {
			throw new IllegalArgumentException(
					"VShare requestor need process by VShareItem");
		}

		BaseVShareItem vShareItem = (BaseVShareItem) item;
		mLastResult = vShareItem.getResultCode();
		mLastResultMessage = vShareItem.getResultMessage();

		// 如果服务端返回值为成功，则继续交给子类进行进一步处理
		if (mLastResult == VShareRequestResult.SUCCESS) {
			handleResult(vShareItem);
		}
	}

	@Override
	protected final void handlePreloadResult(AbstractIFItem item) {
		if (!(item instanceof BaseVShareItem)) {
			throw new IllegalArgumentException(
					"VShare requestor need process by VShareItem");
		}

		BaseVShareItem vShareItem = (BaseVShareItem) item;
		mLastResult = vShareItem.getResultCode();
		mLastResultMessage = vShareItem.getResultMessage();

		// 如果服务端返回值为成功，则继续交给子类进行进一步处理
		if (mLastResult == VShareRequestResult.SUCCESS) {
			handlePreloadResult(vShareItem);
		}
	}

	/**
	 * 获取结果类型
	 * 
	 * @return
	 */
	public VShareRequestResult getLastRequestResult() {
		return mLastResult;
	}

	/**
	 * 过去结果消息
	 * 
	 * @return
	 */
	public String getLastResultMessage() {
		return mLastResultMessage;
	}

	/**
	 * 处理VShareItem解析结果
	 * 
	 * @param item
	 */
	protected abstract void handleResult(BaseVShareItem item);

	/**
	 * 处理VShareItem 预加载解析结果
	 * 
	 * @param item
	 */
	protected void handlePreloadResult(BaseVShareItem item) {
		// 子类可重写，但不作为abstarct方法
	}

	/**
	 * VShare相关接口的返回结果类型
	 * 
	 * @author Calvin
	 * 
	 */
	public static enum VShareRequestResult {
		SUCCESS, FAILED, NORESULT
	}
}
