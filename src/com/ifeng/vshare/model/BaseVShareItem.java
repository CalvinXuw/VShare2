package com.ifeng.vshare.model;

import com.ifeng.util.net.parser.AbstractIFJSONItem;
import com.ifeng.vshare.requestor.BaseVShareRequestor.VShareRequestResult;

/**
 * vshare解析的基类，提供对返回结果的处理
 * 
 * @author Calvin
 * 
 */
public abstract class BaseVShareItem extends AbstractIFJSONItem {

	/**
		 * 
		 */
	private static final long serialVersionUID = -1627595062142057276L;

	/** REUSULT_CODE success */
	private static final int RESULT_CODE_SUCCESS = 100;
	/** 请求结果值 */
	public int mResultCode;
	/** 请求结果消息 */
	public String mResultMessage;

	/**
	 * 构造，针对所有接口进行对返回结果值和返回结果消息的解析
	 */
	public BaseVShareItem() {
		addMappingRuleField("mResultCode", "errno");
		addMappingRuleField("mResultMessage", "errstr");
	}

	/**
	 * 获取结果值
	 * 
	 * @return
	 */
	public VShareRequestResult getResultCode() {
		if (mResultCode == RESULT_CODE_SUCCESS) {
			return VShareRequestResult.SUCCESS;
		}
		return VShareRequestResult.FAILED;
	}

	/**
	 * 获取结果消息
	 * 
	 * @return
	 */
	public String getResultMessage() {
		return mResultMessage;
	}
}
