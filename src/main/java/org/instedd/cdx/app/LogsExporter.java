package org.instedd.cdx.app;

import java.awt.Component;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.JFileChooser;

public class LogsExporter {
  
  private Component parent;
  private CDXSettings settings;

  public LogsExporter(Component parent, CDXSettings settings) {
    this.parent = parent;
    this.settings = settings;
  }
  
  public void run() throws IOException {
    JFileChooser chooser = new JFileChooser();
    chooser.setDialogTitle("Export log to a zip file");
    chooser.setSelectedFile(new File("cdx_client_logs.zip"));
    int result = chooser.showSaveDialog(parent);
    if (result == JFileChooser.APPROVE_OPTION) {
      File selectedFile = chooser.getSelectedFile();
      exportZipFile(selectedFile);
    }
  }

  private void exportZipFile(File outputFile) throws IOException {
    CDXSettings safeSettings = settings.clone();
    safeSettings.deviceKey = null;
    
    FileOutputStream fos = new FileOutputStream(outputFile);
    ZipOutputStream zos = new ZipOutputStream(fos);
    
    exportSettings(zos, safeSettings);
    exportLogs(zos);
    
    zos.close();
    fos.close();
  }

  private void exportSettings(ZipOutputStream zos, CDXSettings safeSettings) throws IOException {
    ZipEntry entry = new ZipEntry("settings.txt");
    zos.putNextEntry(entry);
    safeSettings.toProperties().store(zos, "");
    zos.closeEntry();
  }
  
  private void exportLogs(ZipOutputStream zos) throws IOException {
    ZipEntry entry = new ZipEntry("log.txt");
    zos.putNextEntry(entry);
    
    try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(settings.logPath()))) {
      byte[] bytes = new byte[1024];
      int length;
      while ((length = bis.read(bytes)) > 0) {
        zos.write(bytes, 0, length);
      }
    }
    
    zos.closeEntry();
  }

}
