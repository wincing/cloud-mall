package org.crudboy.cloud.mall.categoryproduct.util;



import lombok.extern.slf4j.Slf4j;
import org.crudboy.cloud.mall.categoryproduct.model.file.FdfsFile;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * FastDFS工具类
 */
@Slf4j
public class FdfsUtil {
    static {
        try {
            Properties properties = new Properties();
            InputStream in = FdfsUtil.class.getClassLoader().getResourceAsStream("fdfs_client.properties");
            properties.load(in);
            ClientGlobal.initByProperties(properties);
        } catch (Exception e) {
            log.error("FastDFS Client Init Fail!",e);
        }
    }

    /**
     * 获取tracker服务端
     */
    private static TrackerServer getTrackerServer() throws IOException {
        TrackerClient trackerClient = new TrackerClient();
        return trackerClient.getConnection();
    }

    /**
     * 获取storage客户端
     */
    private static StorageClient getStorageClient() throws IOException {
        TrackerServer trackerServer = getTrackerServer();
        return new StorageClient(trackerServer, null);
    }

    /**
     * 获取文件信息
     * @param groupName 文件所在storage组名
     * @param remoteFileName 文件存储完整名
     */
    public static FileInfo getFileInfo(String groupName, String remoteFileName) {
        try {
            StorageClient storageClient = getStorageClient();
            return storageClient.get_file_info(groupName, remoteFileName);
        } catch (IOException | MyException e) {
            log.error("Exception: Get File from Fast DFS failed", e);
        }
        return null;
    }

    /**
     * 获取Tracker服务器地址
     */
    public static String getTrackerUrl() throws IOException {
        return "http://" + getTrackerServer().getInetSocketAddress().getHostString() + ":" +
                ClientGlobal.getG_tracker_http_port() + "/";
    }

    /**
     * 文件上传
     */
    public static String[] upload(FdfsFile file) {
        NameValuePair[] metaList = new NameValuePair[1];
        metaList[0] = new NameValuePair("author", file.getName());

        String[] uploadResults = null;
        StorageClient storageClient = null;

        try {
            storageClient = getStorageClient();
            // 文件上传并获取返回结果
            uploadResults = storageClient.upload_appender_file(file.getContent(), file.getExt(), metaList);
        } catch (IOException | MyException e) {
            log.error("Exception when uploading the file:" + file.getName(), e);
        }

        if (uploadResults == null && storageClient!=null) {
            log.error("upload file fail, error code:" + storageClient.getErrorCode());
        }

        return uploadResults;
    }


    /**
     * 文件下载
     * @param groupName 文件所在storage组名
     * @param remoteFileName 文件存储完整名
     */
    public static InputStream downLoad(String groupName, String remoteFileName) {
        try {
            StorageClient storageClient = getStorageClient();
            storageClient.download_file(groupName, remoteFileName);
        } catch (IOException | MyException e) {
            log.error("Exception: Get File from Fast DFS failed", e);
        }
        return null;
    }

    /**
     * 文件删除
     * @param groupName 文件所在storage组名
     * @param remoteFileName 文件存储完整名
     */
    public static boolean delete(String groupName, String remoteFileName) {
        int res = 0;
        try {
            StorageClient storageClient = getStorageClient();
            res = storageClient.delete_file(groupName, remoteFileName);
        } catch (IOException | MyException e) {
            log.error("Exception: Delete File from Fast DFS failed", e);
        }
        return res == 0;
    }

    /**
     * 获取storage组
     * @param groupName storage组名
     */
    public static StorageServer[] getStoreStorages(String groupName) throws IOException {
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = getTrackerServer();
        return trackerClient.getStoreStorages(trackerServer, groupName);
    }

    /**
     * 获取Storage信息,IP和端口
     * @param groupName
     * @param remoteFileName
     */
    public static ServerInfo[] getFetchStorages(String groupName, String remoteFileName) throws IOException {
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = getTrackerServer();
        return trackerClient.getFetchStorages(trackerServer, groupName, remoteFileName);
    }
}
