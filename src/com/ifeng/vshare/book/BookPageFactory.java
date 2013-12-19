package com.ifeng.vshare.book;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.Vector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.Log;

import com.ifeng.BaseApplicaion;
import com.ifeng.util.Utility;

/***
 * @author qianzy
 * @time 2013-6-17 下午3:18:23
 * @describe 绘制文字类
 */
public class BookPageFactory {

	/** tag */
	protected final String TAG = getClass().getSimpleName();
	/** debug开关 */
	protected final boolean DEBUG = BaseApplicaion.DEBUG;
	/** 字符集 */
	private static final String CHARSET = "UTF-8";
	/** context */
	private Context mContext;

	/** 屏幕上下留白 */
	private static final int MARGIN_HEIGHT = 30;
	/** 屏幕左右留白 */
	private static final int MARGIN_WIDTH = 25;

	// 绘制配置信息
	/** 页面宽度 */
	private int mWidth;
	/** 页面高度 */
	private int mHeight;
	/** 文字内容的纵向margin */
	private int mMarginHeight;
	/** 文字内容的横向margin */
	private int mMarginWidth;
	/** 文字内容的宽度 */
	private int mContentWidth;
	/** 文字内容的高度 */
	private int mContentHeight;

	/** 每页可以显示的行数 */
	private int mLineCount;
	/** 正文笔刷 */
	private Paint mContentPaint;
	/** 脚标笔刷 */
	private Paint mInfoPaint;

	/** 绘制的的背景颜色 */
	private int mBackgroundColor = 0xffff9e85;
	/** 绘制的背景图 */
	private Bitmap mBackgroundBitmap;
	/** 内容字号 */
	private int mContentFontSize;
	/** 信息字号 */
	private int mInfoFontSize;
	/** 内容字色 */
	private int mFontColor = Color.rgb(28, 28, 28);

	// 绘制内容信息
	/** 当前章节全部内容文本 */
	private MappedByteBuffer mTextBuffer = null;
	/** 当前章节总长度 */
	private int mChapterLength = 0;
	/** 当前绘制页面的内容 */
	private Vector<String> mCurrentLines = new Vector<String>();
	/** 当前页起始位置 */
	private int mCurrentPageBegin = 0;
	/** 当前页终止位置 */
	private int mCurrentPageEnd = 0;

	// 书籍信息
	/** 章节名 */
	private String mChapterName;
	/** 书名 */
	private String mBookName;

	/** 进度相关callback */
	private ChapterProgressCallback mChapterProgressCallback;

	public BookPageFactory(int w, int h, int contentSize, int infoSize,
			Context context) {
		mContext = context;
		mContentFontSize = (int) (contentSize * Utility.getDensity(mContext));
		mInfoFontSize = (int) (infoSize * Utility.getDensity(mContext));
		mWidth = w;
		mHeight = h;

		mMarginHeight = (int) (MARGIN_HEIGHT * Utility.getDensity(mContext));
		mMarginWidth = (int) (MARGIN_WIDTH * Utility.getDensity(mContext));

		mContentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);// 画笔
		mContentPaint.setTextAlign(Align.LEFT);// 左对齐
		mContentPaint.setTextSize(mContentFontSize);// 字体大小
		mContentPaint.setColor(mFontColor);// 字体颜色

		mInfoPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mInfoPaint.setTextAlign(Align.CENTER);
		mInfoPaint.setTextSize(mInfoFontSize);
		mInfoPaint.setColor(mFontColor);
		mContentWidth = mWidth - mMarginWidth;
		mContentHeight = (int) (mHeight - mMarginHeight - mInfoFontSize - mInfoFontSize);
		// 1.5倍行距
		mLineCount = (int) (mContentHeight / (mContentFontSize * 1.5));
	}

	/**
	 * 
	 * @param strFilePath
	 * @param begin
	 *            表示进度，读取进度时，将begin值给 {@link #mCurrentPageBegin}作为开始位置进度记录
	 * @throws IOException
	 */
	public void openbook(String strFilePath, int begin) throws IOException {
		File book_file = new File(strFilePath);
		long lLen = book_file.length();
		mChapterLength = (int) lLen;
		mTextBuffer = new RandomAccessFile(book_file, "r").getChannel().map(
				FileChannel.MapMode.READ_ONLY, 0, lLen);

		mCurrentPageBegin = begin;
		mCurrentPageEnd = begin;
	}

	/**
	 * 添加回调
	 * 
	 * @param callback
	 */
	public void setChapterProgressCallback(ChapterProgressCallback callback) {
		mChapterProgressCallback = callback;
	}

	/**
	 * 是否为首页，用于判断跨章阅读
	 * 
	 * @return
	 */
	public boolean isFirstPage() {
		return mCurrentPageBegin <= 0;
	}

	/**
	 * 是否末页，用于判断跨章阅读
	 * 
	 * @return
	 */
	public boolean isLastPage() {
		return mCurrentPageEnd >= mChapterLength;
	}

	/**
	 * 向前翻页
	 */
	public void turePrePage() {
		if (mCurrentPageBegin <= 0) {
			return;
		}
		mCurrentLines.clear();
		mCurrentPageBegin = getPrePageBegin();
		mCurrentLines = getPageContent();
	}

	/**
	 * 向后翻页
	 */
	public void tureNextPage() {
		if (mCurrentPageEnd >= mChapterLength) {
			return;
		}
		mCurrentLines.clear();
		mCurrentPageBegin = mCurrentPageEnd;// 下一页页起始位置=当前页结束位置
		mCurrentLines = getPageContent();
	}

	/**
	 * 翻到上一章
	 */
	public void turnPreChapter() {
		if (mCurrentPageBegin <= 0) {
			return;
		}
		mCurrentLines.clear();
		mCurrentPageBegin = mCurrentPageEnd = getLastPageBegin();
		mCurrentLines = getPageContent();
	}

	/**
	 * 翻到下一章
	 */
	public void turnNextChapter() {
		mCurrentLines.clear();
		mCurrentPageBegin = mCurrentPageEnd = 0;
		mCurrentLines = getPageContent();
	}

	/**
	 * 绘制当前页面
	 * 
	 * @param c
	 */
	public void drawPage(Canvas c) {
		if (mCurrentLines.size() == 0) {
			mCurrentLines = getPageContent();
		}
		if (mCurrentLines.size() > 0) {
			if (mBackgroundBitmap == null) {
				c.drawColor(mBackgroundColor);
			} else {
				c.drawBitmap(mBackgroundBitmap, 0, 0, null);
			}
			int y = (int) (mMarginHeight / 2 + mInfoFontSize);
			for (String strLine : mCurrentLines) {
				y += (mContentFontSize * 1.5);
				c.drawText(strLine, mMarginWidth / 2, y, mContentPaint);
			}
		}

		// 计算阅读进度
		float current = mCurrentPageBegin
				+ mChapterProgressCallback.getChapterStartProgress();
		int total = mChapterProgressCallback.getBookTotalLength();
		float fPercent = current / total;
		DecimalFormat df = new DecimalFormat("#0.0");
		String strPercent = df.format(fPercent * 100) + "%";

		// 计算书名
		String bookName = null;
		if (mBookName.length() > 12) {
			bookName = mBookName.substring(0, 5)
					+ "..."
					+ mBookName.substring(mBookName.length() - 5,
							mBookName.length());
		} else {
			bookName = mBookName;
		}

		int nPercentWidth = (int) mContentPaint.measureText("999.9%") + 1;
		c.drawText(strPercent, mWidth - nPercentWidth,
				(float) (mHeight - mMarginHeight / 2), mInfoPaint);
		c.drawText(bookName, mWidth / 2, (float) (mHeight - mMarginHeight / 2),
				mInfoPaint);
		c.drawText(mChapterName, mWidth / 2,
				(float) (mMarginHeight / 2 + mInfoFontSize), mInfoPaint);

		System.gc();
	}

	/**
	 * 为向前跨章翻页提供进度位置
	 * 
	 * @return
	 */
	private int getLastPageBegin() {
		mContentPaint.setTextSize(mContentFontSize);
		mContentPaint.setColor(mFontColor);

		mCurrentPageBegin = mCurrentPageEnd = 0;
		while (mCurrentPageEnd < mChapterLength) {
			mCurrentPageBegin = mCurrentPageEnd;
			getPageContent();
		}

		return mCurrentPageBegin;
	}

	/**
	 * 根据当前起始进度，计算下一页将要展示的文字内容
	 * 
	 * @return 下一页的内容 Vector<String>
	 */
	private Vector<String> getPageContent() {
		mContentPaint.setTextSize(mContentFontSize);
		mContentPaint.setColor(mFontColor);
		String strParagraph = "";
		Vector<String> lines = new Vector<String>();
		while (lines.size() < mLineCount && mCurrentPageEnd < mChapterLength) {
			byte[] paraBuf = readForwardParagraph(mCurrentPageEnd);
			mCurrentPageEnd += paraBuf.length;// 每次读取后，记录结束点位置，该位置是段落结束位置
			try {
				strParagraph = new String(paraBuf, CHARSET);// 转换成制定UTF-8编码
			} catch (UnsupportedEncodingException e) {
				Log.e(TAG, "pageDown->转换编码失败", e);
			}
			String strReturn = "";
			// 替换掉回车换行符
			if (strParagraph.indexOf("\r\n") != -1) {
				strReturn = "\r\n";
				strParagraph = strParagraph.replaceAll("\r\n", "");
			} else if (strParagraph.indexOf("\n") != -1) {
				strReturn = "\n";
				strParagraph = strParagraph.replaceAll("\n", "");
			}

			if (strParagraph.length() == 0) {
				lines.add(strParagraph);
			}
			while (strParagraph.length() > 0) {
				// 画一行文字
				int nSize = mContentPaint.breakText(strParagraph, true,
						mContentWidth, null);
				lines.add(strParagraph.substring(0, nSize));
				strParagraph = strParagraph.substring(nSize);// 得到剩余的文字
				// 超出最大行数则不再画
				if (lines.size() >= mLineCount) {
					break;
				}
			}
			// 如果该页最后一段只显示了一部分，则从新定位结束点位置
			if (strParagraph.length() != 0) {
				try {
					mCurrentPageEnd -= (strParagraph + strReturn)
							.getBytes(CHARSET).length;
				} catch (UnsupportedEncodingException e) {
					Log.e(TAG, "pageDown->记录结束点位置失败", e);
				}
			}
		}
		return lines;
	}

	/**
	 * 得到上页的起始位置
	 */
	private int getPrePageBegin() {
		if (mCurrentPageBegin < 0)
			mCurrentPageBegin = 0;
		mContentPaint.setTextSize(mContentFontSize);
		mContentPaint.setColor(mFontColor);
		Vector<String> lines = new Vector<String>();
		String strParagraph = "";
		while (lines.size() < mLineCount && mCurrentPageBegin > 0) {
			Vector<String> paraLines = new Vector<String>();
			byte[] paraBuf = readBackwardParagraph(mCurrentPageBegin);
			mCurrentPageBegin -= paraBuf.length;// 每次读取一段后,记录开始点位置,是段首开始的位置
			try {
				strParagraph = new String(paraBuf, CHARSET);
			} catch (UnsupportedEncodingException e) {
				Log.e(TAG, "pageUp->转换编码失败", e);
			}
			strParagraph = strParagraph.replaceAll("\r\n", "");
			strParagraph = strParagraph.replaceAll("\n", "");
			// 如果是空白行，直接添加
			if (strParagraph.length() == 0) {
				paraLines.add(strParagraph);
			}
			while (strParagraph.length() > 0) {
				// 将一段文字按照内容宽度进行分段
				int textCountInLine = mContentPaint.breakText(strParagraph,
						true, mContentWidth, null);
				paraLines.add(strParagraph.substring(0, textCountInLine));
				strParagraph = strParagraph.substring(textCountInLine);
			}
			lines.addAll(0, paraLines);
		}

		while (lines.size() > mLineCount) {
			try {
				mCurrentPageBegin += lines.get(0).getBytes(CHARSET).length;
				lines.remove(0);
			} catch (UnsupportedEncodingException e) {
				Log.e(TAG, "pageUp->记录起始点位置失败", e);
			}
		}
		mCurrentPageEnd = mCurrentPageBegin;// 上上一页的结束点等于上一页的起始点
		return mCurrentPageBegin;
	}

	/**
	 * 读取指定位置的上一个段落
	 * 
	 * @param nFromPos
	 * @return byte[]
	 */
	private byte[] readBackwardParagraph(int nFromPos) {
		int nEnd = nFromPos;
		int i;
		byte b0, b1;
		if (CHARSET.equals("UTF-16LE")) {
			i = nEnd - 2;
			while (i > 0) {
				b0 = mTextBuffer.get(i);
				b1 = mTextBuffer.get(i + 1);
				if (b0 == 0x0a && b1 == 0x00 && i != nEnd - 2) {
					i += 2;
					break;
				}
				i--;
			}

		} else if (CHARSET.equals("UTF-16BE")) {
			i = nEnd - 2;
			while (i > 0) {
				b0 = mTextBuffer.get(i);
				b1 = mTextBuffer.get(i + 1);
				if (b0 == 0x00 && b1 == 0x0a && i != nEnd - 2) {
					i += 2;
					break;
				}
				i--;
			}
		} else {
			i = nEnd - 1;
			while (i > 0) {
				b0 = mTextBuffer.get(i);
				if (b0 == 0x0a && i != nEnd - 1) {// 0x0a表示换行符
					i++;
					break;
				}
				i--;
			}
		}
		if (i < 0)
			i = 0;
		int nParaSize = nEnd - i;
		int j;
		byte[] buf = new byte[nParaSize];
		for (j = 0; j < nParaSize; j++) {
			buf[j] = mTextBuffer.get(i + j);
		}
		return buf;
	}

	/**
	 * 读取指定位置的下一个段落
	 * 
	 * @param nFromPos
	 * @return byte[]
	 */
	private byte[] readForwardParagraph(int nFromPos) {
		int nStart = nFromPos;
		int i = nStart;
		byte b0, b1;
		// 根据编码格式判断换行
		if (CHARSET.equals("UTF-16LE")) {
			while (i < mChapterLength - 1) {
				b0 = mTextBuffer.get(i++);
				b1 = mTextBuffer.get(i++);
				if (b0 == 0x0a && b1 == 0x00) {
					break;
				}
			}
		} else if (CHARSET.equals("UTF-16BE")) {
			while (i < mChapterLength - 1) {
				b0 = mTextBuffer.get(i++);
				b1 = mTextBuffer.get(i++);
				if (b0 == 0x00 && b1 == 0x0a) {
					break;
				}
			}
		} else {
			while (i < mChapterLength) {
				b0 = mTextBuffer.get(i++);
				if (b0 == 0x0a) {
					break;
				}
			}
		}
		int nParaSize = i - nStart;
		byte[] buf = new byte[nParaSize];
		for (i = 0; i < nParaSize; i++) {
			buf[i] = mTextBuffer.get(nFromPos + i);
		}
		return buf;
	}

	/**
	 * 获取当前页面在本章中的进度
	 * 
	 * @return
	 */
	public int getCurrentPageProgress() {
		return mCurrentPageBegin;
	}

	/**
	 * 设置当前绘制背景
	 * 
	 * @param background
	 */
	public void setBackgroundBitmap(Bitmap background) {
		mBackgroundBitmap = Bitmap.createScaledBitmap(background,
				(int) Utility.getScreenWidth(mContext),
				(int) Utility.getScreenHeight(mContext), true);
	}

	/**
	 * 设置字号
	 * 
	 * @param fontSize
	 */
	public void setFontSize(int fontSize) {
		this.mContentFontSize = fontSize;
		mLineCount = (int) (mContentHeight / (fontSize * 1.5)) - 1;
	}

	/**
	 * 设置字色
	 * 
	 * @param fontColor
	 */
	public void setFontColor(int fontColor) {
		this.mFontColor = fontColor;
	}

	/**
	 * 设置章节名
	 * 
	 * @param chapterName
	 * @return
	 */
	public String setChapterName(String chapterName) {
		return mChapterName = chapterName;
	}

	/**
	 * 设置书名
	 * 
	 * @param bookName
	 * @return
	 */
	public String setBookName(String bookName) {
		return mBookName = bookName;
	}

	/**
	 * 释放
	 */
	public void release() {
		if (mBackgroundBitmap != null && !mBackgroundBitmap.isRecycled()) {
			mBackgroundBitmap.recycle();
		}
	}

	/**
	 * 获取进度信息的回调接口，用于获取数据计算本章页面在全本书中的进度
	 * 
	 * @author Calvin
	 * 
	 */
	public interface ChapterProgressCallback {
		/**
		 * 获取本章起始位置在全书的进度
		 * 
		 * @return
		 */
		public int getChapterStartProgress();

		/**
		 * 获取整本书的长度
		 * 
		 * @return
		 */
		public int getBookTotalLength();
	}

}
