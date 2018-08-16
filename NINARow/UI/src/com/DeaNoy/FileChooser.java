package com.DeaNoy;

//TODO: swing is no good, need javaFX.
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;

public class FileChooser {

    private JFileChooser mFileChooser;

    public FileChooser() {
        this.mFileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
    }

    public String LoadFile() {
        File selectedFile;
        int returnValue;
        returnValue = mFileChooser.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            selectedFile = mFileChooser.getSelectedFile();
            return selectedFile.getAbsolutePath();
        }
        else if (returnValue == JFileChooser.CANCEL_OPTION){
            throw new RuntimeException("You selected nothing");
        }
        else if (returnValue == JFileChooser.ERROR_OPTION){
            throw new RuntimeException("Error while loading file with file chooser");
        }

        return null;
    }

}