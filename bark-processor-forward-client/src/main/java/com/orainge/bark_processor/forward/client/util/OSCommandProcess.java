package com.orainge.bark_processor.forward.client.util;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Accessors(chain = true)
public class OSCommandProcess extends Thread {
    private String[] command;

    public OSCommandProcess() {
    }

    public OSCommandProcess(String... command) {
        this.command = command;
    }

    public static OSCommandProcess build() {
        return new OSCommandProcess();
    }

    public String[] getCommand() {
        return command;
    }

    public OSCommandProcess setCommand(String... command) {
        this.command = command;
        return this;
    }

    @Override
    public void run() {
        int exitVal = 0;

        try {
            Process process = Runtime.getRuntime().exec(command);
            new RedirectCmdStreamThread(process.getErrorStream(), "ERR").start();
            new RedirectCmdStreamThread(process.getInputStream(), "INFO").start();

            exitVal = process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        if (exitVal != 0) {
            throw new RuntimeException("命令执行失败");
        }
    }

    private static class RedirectCmdStreamThread extends Thread {
        InputStream is;
        String printType;

        RedirectCmdStreamThread(InputStream is, String printType) {
            this.is = is;
            this.printType = printType;
        }

        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println(printType + ">" + line);
                }

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}
