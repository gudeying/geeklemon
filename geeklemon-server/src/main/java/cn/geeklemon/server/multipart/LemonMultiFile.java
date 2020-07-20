package cn.geeklemon.server.multipart;

import io.netty.handler.codec.http.multipart.DiskAttribute;
import io.netty.handler.codec.http.multipart.DiskFileUpload;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;

import java.io.File;
import java.io.IOException;

/**
 * @author : Kavin Gu Project Name : redant Description :
 * @version : ${VERSION} 2019/2/19 13:07 Modified by : kavingu
 */
public class LemonMultiFile implements MultiPartFile {

    private FileUpload fileUpload;

    static {
        DiskFileUpload.deleteOnExitTemporaryFile = true;
        DiskFileUpload.baseDirectory = null;
        DiskAttribute.deleteOnExitTemporaryFile = true;
        DiskAttribute.baseDirectory = null;
    }

    public LemonMultiFile(FileUpload fileUpload) {
        this.fileUpload = fileUpload;
    }

    @Override
    public String getName() {
        return this.fileUpload.getFilename();
    }

    @Override
    public boolean transferTo(File dest) throws IOException {
        // 使用的是 fileChannel的transferTo，零拷贝
        boolean result = this.fileUpload.renameTo(dest);
        InterfaceHttpData release = (InterfaceHttpData) fileUpload;
        try {
            release.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public long length() {
        try {
            return this.fileUpload.getFile().length();
        } catch (IOException e) {
            return 0L;
        }
    }
}