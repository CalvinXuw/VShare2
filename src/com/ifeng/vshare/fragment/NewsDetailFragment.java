package com.ifeng.vshare.fragment;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ifeng.util.logging.Log;
import com.ifeng.util.model.AbstractModel;
import com.ifeng.util.net.requestor.IRequestModelErrorCode;
import com.ifeng.util.ui.IFWebView;
import com.ifeng.util.ui.IFWebView.HtmlMappingItem;
import com.ifeng.util.ui.IFWebView.OnJSClick;
import com.ifeng.util.ui.StateView;
import com.ifeng.vshare.R;
import com.ifeng.vshare.VShareFragment;
import com.ifeng.vshare.activity.NewsDetailPhotoActivity;
import com.ifeng.vshare.activity.VideoPlayActivity;
import com.ifeng.vshare.model.NewsDetailItem;
import com.ifeng.vshare.requestor.BaseVShareRequestor;
import com.ifeng.vshare.requestor.BaseVShareRequestor.VShareRequestResult;
import com.ifeng.vshare.requestor.NewsDetailRequestor;
import com.ifeng.vshare.ui.state.ErrorRequestState;

/**
 * @author qianzy
 * @time 2013-6-4 上午10:13:36
 * @describe 新闻详情fragment
 */
@SuppressLint("SetJavaScriptEnabled")
public class NewsDetailFragment extends VShareFragment {

	/** key id */
	private static final String KEY_ID = "id";
	/** key title */
	private static final String KEY_TITLE = "title";

	/** css file name 位于assets */
	private static final String CSS_FILENAME = "default_html_css.html";

	/**
	 * 获取实例
	 * 
	 * @param id
	 * @return
	 */
	public static NewsDetailFragment getInstance(int id, String title) {
		NewsDetailFragment detailFragment = new NewsDetailFragment();
		Bundle arg = new Bundle();
		arg.putInt(KEY_ID, id);
		arg.putString(KEY_TITLE, title);
		detailFragment.setArguments(arg);
		return detailFragment;
	}

	/** key model requestor */
	private static final String KEY_MODEL_REQUESTOR = "requestor";

	/** state view 当前状态 */
	private StateView mStateView;
	/** webview */
	private IFWebView mWebView;
	/** detail requestor */
	private NewsDetailRequestor mDetailRequestor;

	/**
	 * 构造
	 */
	public NewsDetailFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(new Bundle());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View layout = inflater.inflate(R.layout.news_detail_content, container,
				false);

		mStateView = (StateView) layout.findViewById(R.id.stateview);
		mWebView = (IFWebView) layout.findViewById(R.id.webview_news_detail);

		// 设置状态
		mStateView.setState(mProcessingRequestState);

		// 发起请求
		mDetailRequestor.request();

		return layout;
	}

	/*
	 * 处理网络请求成功的数据，其中需要根据RefreshListView的状态进行变更
	 * 
	 * @see
	 * com.ifeng.util.net.requestor.AbstractRequestor.OnRequestorListener#onSuccess
	 * (com.ifeng.util.net.requestor.AbstractRequestor)
	 */

	@Override
	public void onSuccess(AbstractModel requestor) {
		// 筛除服务器返回错误
		if (((BaseVShareRequestor) requestor).getLastRequestResult() != VShareRequestResult.SUCCESS) {
			onFailed(requestor, IRequestModelErrorCode.ERROR_CODE_SERVICE_ERROR);
			return;
		} else {
			// 请求完成，结束等待页面
			mStateView.dismiss();
		}

		updateDatas((NewsDetailRequestor) requestor);
	}

	/**
	 * 更新数据
	 * 
	 * @param requestor
	 */
	private synchronized void updateDatas(NewsDetailRequestor requestor) {

		// 整合本地样式和服务端返回的html页面数据
		NewsDetailItem item = requestor.getDetailItem();
		String htmlData = item.mHtmlData;
		String styleData = getCss();

		final List<HtmlMappingItem> mappingList = mWebView.loadNewsHtml(
				htmlData + styleData, new OnJSClick() {
					/**
					 * 跳转显示html大图
					 */
					@Override
					public void onImageClick(final String url) {
						getActivity().startActivity(
								NewsDetailPhotoActivity.getIntent(
										getActivity(), url));
					}

					/**
					 * 跳转播放视频
					 */
					@Override
					public void onVideoClick(String url) {
						getActivity().startActivity(
								VideoPlayActivity.getIntent(getActivity(),
										getArguments().getString(KEY_TITLE),
										getArguments().getInt(KEY_ID)));
					}
				});
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				Iterator<HtmlMappingItem> itMapping = mappingList.iterator();
				while (itMapping.hasNext()) {
					final HtmlMappingItem item = itMapping.next();
					mImageFetcher.loadImage(
							item.getOrlUrl(),
							new com.ifeng.util.imagecache.ImageWorker.ImageFilepathCallback() {
								@Override
								public void getImageFilePath(String filePath) {
									item.setTargetUri(filePath);
									mWebView.resetImage(item);
								}
							});

				}
			}
		});
	}

	/**
	 * 获取css样式，若读取失败则返回空字符
	 * 
	 * @return
	 */
	private String getCss() {
		try {
			InputStreamReader inputReader = new InputStreamReader(
					getResources().getAssets().open(CSS_FILENAME));
			BufferedReader bufReader = new BufferedReader(inputReader);
			String line = null;
			StringBuffer result = new StringBuffer();
			while ((line = bufReader.readLine()) != null) {
				result.append(line);
			}
			return result.toString();
		} catch (Exception e) {
			if (DEBUG) {
				Log.e(TAG, e);
			}
			return "";
		}
	}

	/*
	 * 处理网络请求失败的数据，其中需要根据RefreshListView的状态进行变更
	 * 
	 * @see
	 * com.ifeng.util.net.requestor.AbstractRequestor.OnRequestorListener#onFailed
	 * (com.ifeng.util.net.requestor.AbstractRequestor,
	 * com.ifeng.util.net.requestor.IRequestErrorCode)
	 */

	@Override
	public void onFailed(AbstractModel requestor, int errorCode) {
		mStateView.setState(mErrorRequestState);
	}

	/*
	 * 接收自StateView的状态信息，其中状态id为子类指定id，处理页面整体的显示状态，错误、无数据、正常等
	 * 
	 * @see
	 * com.ifeng.util.ui.StateView.State.OnStateActionListener#onActionTrigger
	 * (int)
	 */

	@Override
	public void onActionTrigger(int actionId) {
		if (actionId == ErrorRequestState.STATE_ACTION_ERROR_RETRY) {
			mStateView.setState(mProcessingRequestState);
			mDetailRequestor.request();
		}
	}

	@Override
	protected void setupModel() {
		mDetailRequestor = new NewsDetailRequestor(getActivity(),
				getArguments().getInt(KEY_ID), this);
		mModelManageQueue.addTaskModel(KEY_MODEL_REQUESTOR, mDetailRequestor);
	}

	@Override
	protected void setImageCacheParams() {
		mImageSize = mWidth;
		mImageCacheDir = "newsdetail";
	}

}
