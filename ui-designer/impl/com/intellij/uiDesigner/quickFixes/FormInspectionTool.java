package com.intellij.uiDesigner.quickFixes;

import com.intellij.psi.PsiElement;
import com.intellij.uiDesigner.ErrorInfo;
import com.intellij.uiDesigner.RadComponent;
import com.intellij.uiDesigner.designSurface.GuiEditor;
import com.intellij.uiDesigner.lw.IRootContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author yole
 */
public interface FormInspectionTool {
  void startCheckForm(IRootContainer radRootContainer);
  void doneCheckForm(IRootContainer radRootContainer);

  @Nullable
  ErrorInfo[] checkComponent(@NotNull GuiEditor editor, @NotNull RadComponent component);

  boolean isActive(PsiElement psiRoot);

}
