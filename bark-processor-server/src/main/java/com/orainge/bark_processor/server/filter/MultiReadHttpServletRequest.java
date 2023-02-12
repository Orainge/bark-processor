package com.orainge.bark_processor.server.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;

/**
 * 多次读写 BODY 用 HTTP REQUEST <br>
 * 解决流只能读一次问题
 *
 * @author orainge
 * @since 2022/3/2
 */
@Slf4j
public class MultiReadHttpServletRequest extends HttpServletRequestWrapper {
    private final byte[] body;

    public MultiReadHttpServletRequest(HttpServletRequest request) {
        super(request);
        this.body = getBodyByteArray(request);
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream bais = new ByteArrayInputStream(body);

        return new ServletInputStream() {
            @Override
            public int read() throws IOException {
                return bais.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
            }
        };
    }

    /**
     * 获取请求Body
     */
    private byte[] getBodyByteArray(HttpServletRequest request) {
        InputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        try {
            inputStream = request.getInputStream();
            outputStream = new ByteArrayOutputStream();
            byte[] buff = new byte[100];
            int rc = 0;
            while ((rc = inputStream.read(buff, 0, 100)) > 0) {
                outputStream.write(buff, 0, rc);
            }
            return outputStream.toByteArray();
        } catch (Exception e) {
            return new byte[0];
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
