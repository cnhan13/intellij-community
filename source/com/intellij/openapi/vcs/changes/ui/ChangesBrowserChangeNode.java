/*
 * Copyright (c) 2000-2007 JetBrains s.r.o. All Rights Reserved.
 */

package com.intellij.openapi.vcs.changes.ui;

import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangesUtil;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.util.Icons;

import java.awt.*;
import java.io.File;

/**
 * @author yole
 */
public class ChangesBrowserChangeNode extends ChangesBrowserNode<Change> {
  private Project myProject;

  protected ChangesBrowserChangeNode(final Project project, Change userObject) {
    super(userObject);
    myProject = project;
    if (!ChangesUtil.getFilePath(userObject).isDirectory()) {
      myCount = 1;
    }
  }

  @Override
  protected boolean isDirectory() {
    return ChangesUtil.getFilePath(getUserObject()).isDirectory();
  }

  @Override
  public void render(final ChangesBrowserNodeRenderer renderer, final boolean selected, final boolean expanded, final boolean hasFocus) {
    final Change change = getUserObject();
    final FilePath filePath = ChangesUtil.getFilePath(change);
    final String fileName = filePath.getName();
    VirtualFile vFile = filePath.getVirtualFile();
    final Color changeColor = change.getFileStatus().getColor();
    renderer.appendFileName(vFile, fileName, changeColor);

    if (change.isRenamed() || change.isMoved()) {
      FilePath beforePath = change.getBeforeRevision().getFile();
      if (change.isRenamed()) {
        renderer.append(" - renamed from "+ beforePath.getName(), SimpleTextAttributes.REGULAR_ATTRIBUTES);
      }
      else {
        renderer.append(" - moved from " + change.getMoveRelativePath(myProject), SimpleTextAttributes.REGULAR_ATTRIBUTES);
      }
    }

    if (renderer.isShowFlatten()) {
      final File parentFile = filePath.getIOFile().getParentFile();
      if (parentFile != null) {
        renderer.append(" (" + parentFile.getPath() + ")", SimpleTextAttributes.GRAYED_ATTRIBUTES);
      }
    }
    else if (getCount() != 1 || getDirectoryCount() != 0) {
      appendCount(renderer);
    }

    if (filePath.isDirectory()) {
      renderer.setIcon(Icons.DIRECTORY_CLOSED_ICON);
    }
    else {
      renderer.setIcon(filePath.getFileType().getIcon());
    }
  }

  @Override
  public String getTextPresentation() {
    return ChangesUtil.getFilePath(getUserObject()).getName();
  }

  @Override
  public String toString() {
    return FileUtil.toSystemDependentName(ChangesUtil.getFilePath(getUserObject()).getPath());
  }
}