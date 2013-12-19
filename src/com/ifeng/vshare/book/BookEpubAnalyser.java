package com.ifeng.vshare.book;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.http.protocol.HTTP;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;

import com.ifeng.util.Utility;
import com.ifeng.util.download.DownloadInfo;
import com.ifeng.util.download.DownloadManager;
import com.ifeng.util.logging.Log;
import com.ifeng.util.model.AbstractModel;
import com.ifeng.util.model.IModelErrorCode;
import com.ifeng.util.net.parser.AbstractIFXMLItem;
import com.ifeng.vshare.database.dao.BooksManageDao.BooksTaskItem;

/**
 * epub图书解析器
 * 
 * @author Calvin
 * 
 */
public class BookEpubAnalyser extends AbstractModel {

	/** ops文件夹 */
	private static final String OPS = File.separator + "OPS";
	/** opf文件位置 */
	private static final String FB_OPF = File.separator + "fb.opf";
	/** 解压文件标识 */
	static final String UNZIP_DIR_TAG = "_unzip";
	/** 配置文件标识 */
	static final String MANIFEST_FILENAME = "ifeng_manifest";
	/** 解析完成的章节扩展名 */
	private static final String EXTENSIONS_CHAPTER = ".chapter";

	/** downloaddb dao */
	private DownloadManager mDownloadManager;
	/** 需要处理的item */
	private BooksTaskItem mBooksTaskItem;

	public BookEpubAnalyser(Context context, BooksTaskItem item,
			OnModelProcessListener listener) {
		super(context, listener);
		mBooksTaskItem = item;
	}

	@Override
	protected void init() {
		mDownloadManager = new DownloadManager(mContext.getContentResolver(),
				mContext.getPackageName());
	}

	@Override
	protected void process() {
		try {
			DownloadInfo downloadInfo = mDownloadManager.getDownloadById(
					mContext, mBooksTaskItem.mDownloadId);
			// 数据已经丢失
			if (downloadInfo == null) {
				onFailed(IModelErrorCode.ERROR_CODE_UNKNOW);
				return;
			}
			// 解压文件
			unzip(new FileInputStream(downloadInfo.mFileName),
					downloadInfo.mFileName + UNZIP_DIR_TAG);

			// 解析fb.opf文件
			EpubCatalogueItem catalogueItem = new EpubCatalogueItem();
			catalogueItem.parseData(Utility
					.getStringFromInput(new FileInputStream(
							downloadInfo.mFileName + UNZIP_DIR_TAG + OPS
									+ FB_OPF)));
			List<ChapterItem> chapterItems = catalogueItem.getChapters();

			// 解析每章html文档
			for (ChapterItem chapterItem : chapterItems) {
				// ****_unzip/OPS/xxxx
				parseHtmlDocument(downloadInfo.mFileName + UNZIP_DIR_TAG + OPS
						+ File.separator + chapterItem.mFileName);
				chapterItem.mFileName = downloadInfo.mFileName + UNZIP_DIR_TAG
						+ OPS + File.separator + chapterItem.mFileName
						+ EXTENSIONS_CHAPTER;
			}

			// 序列化ChapterItem到配置文件
			FileOutputStream fos = new FileOutputStream(downloadInfo.mFileName
					+ UNZIP_DIR_TAG + File.separator
					+ BookEpubAnalyser.MANIFEST_FILENAME);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(chapterItems);
			oos.flush();
			oos.close();

			onSuccess();
		} catch (Exception e) {
			if (DEBUG) {
				Log.e(TAG, "occur error when analysering epub");
				Log.e(TAG, e);
			}
			onFailed(IModelErrorCode.ERROR_CODE_UNKNOW);
		}
	}

	/**
	 * 开始解析
	 */
	public void analyse() {
		executeAsyncTask();
	}

	/**
	 * unzip epub file
	 * 
	 * @param zipFileName
	 * @param outputDirectory
	 * @throws IOException
	 */
	public void unzip(InputStream zipFileName, String outputDirectory)
			throws IOException {
		try {
			ZipInputStream in = new ZipInputStream(zipFileName);
			// 获取ZipInputStream中的ZipEntry条目，一个zip文件中可能包含多个ZipEntry，
			// 当getNextEntry方法的返回值为null，则代表ZipInputStream中没有下一个ZipEntry，
			// 输入流读取完成；
			ZipEntry entry = in.getNextEntry();
			while (entry != null) {

				// 创建以zip包文件名为目录名的根目录
				File file = new File(outputDirectory);
				file.mkdirs();
				if (entry.isDirectory()) {
					String name = entry.getName();
					name = name.substring(0, name.length() - 1);

					file = new File(outputDirectory + File.separator + name);
					file.mkdirs();

				} else {
					file = new File(outputDirectory + File.separator
							+ entry.getName());
					file.createNewFile();
					FileOutputStream out = new FileOutputStream(file);
					int read;
					byte[] buffer = new byte[1024];
					while ((read = in.read(buffer)) != -1) {
						out.write(buffer, 0, read);
					}
					out.close();
				}
				// 读取下一个ZipEntry
				entry = in.getNextEntry();
			}
			in.close();
			return;
		} catch (FileNotFoundException e) {
			if (DEBUG) {
				Log.e(TAG, "file not exist when unzip epub");
				Log.e(TAG, e);
			}
		} catch (IOException e) {
			if (DEBUG) {
				Log.e(TAG, "occur error when unzip epub");
				Log.e(TAG, e);
			}
		}
		throw new IOException();
	}

	/**
	 * 解析每章html页面，生成新的解析过的文件
	 * 
	 * @param fileName
	 * @throws IOException
	 */

	private void parseHtmlDocument(String fileName) throws IOException {
		FileWriter fw = new FileWriter(fileName + EXTENSIONS_CHAPTER);
		try {
			Document doc = Jsoup.parse(new File(fileName), HTTP.UTF_8);
			Elements es = doc.getElementsByTag("p");
			for (Element e : es) {
				String textInP = e.text();
				fw.write("    " + textInP + "\n");
			}
		} catch (Exception e) {
			if (DEBUG) {
				Log.e(TAG, "occur error when parse html " + fileName);
				Log.e(TAG, e);
			}
			throw new IOException();
		}
		if (fw != null) {
			fw.flush();
			fw.close();
		}
	}

	/**
	 * 解析epub中的 OPS/fb.opf 配置文件
	 * 
	 * @author Calvin
	 * 
	 */
	public static class EpubCatalogueItem extends AbstractIFXMLItem {

		/**
		 * 
		 */
		private static final long serialVersionUID = -1556769368186169997L;
		/** 对应章节item */
		public List<ChapterItem> mChapterItems;

		/**
		 * 构造
		 */
		public EpubCatalogueItem() {
			addMappingRuleArrayField("mChapterItems", "guide",
					ChapterItem.class);
		}

		/**
		 * 获取章节列表
		 * 
		 * @return
		 */
		public List<ChapterItem> getChapters() {
			List<ChapterItem> toRemove = new LinkedList<ChapterItem>();
			for (ChapterItem chapterItem : mChapterItems) {
				if (!chapterItem.mFileName.contains("chapter")) {
					toRemove.add(chapterItem);
				}
			}
			mChapterItems.removeAll(toRemove);
			for (int i = 0; i < mChapterItems.size(); i++) {
				ChapterItem chapterItem = mChapterItems.get(i);
				// 填充章节号码
				chapterItem.mChapterId = i + 1;
			}
			return mChapterItems;
		}
	}

	/**
	 * 嵌套解析的章节item
	 * 
	 * @author Calvin
	 * 
	 */
	public static class ChapterItem extends AbstractIFXMLItem {
		/**
		 * 
		 */
		private static final long serialVersionUID = -5335314746355686286L;
		/** 章节标题 */
		public String mTitle;
		/** 章节文件所在 */
		public String mFileName;
		/** 章节序号 */
		public int mChapterId;

		/**
		 * 构造
		 */
		public ChapterItem() {
			addMappingRuleField("mTitle", ":title");
			addMappingRuleField("mFileName", ":href");
		}
	}

}
