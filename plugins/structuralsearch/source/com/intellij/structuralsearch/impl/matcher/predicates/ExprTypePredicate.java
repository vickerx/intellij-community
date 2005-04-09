package com.intellij.structuralsearch.impl.matcher.predicates;

import com.intellij.structuralsearch.impl.matcher.handlers.Handler;
import com.intellij.structuralsearch.impl.matcher.MatchContext;
import com.intellij.structuralsearch.impl.matcher.MatchUtils;
import com.intellij.structuralsearch.impl.matcher.iterators.NodeIterator;
import com.intellij.structuralsearch.impl.matcher.iterators.HierarchyNodeIterator;
import com.intellij.psi.*;

/**
 * Created by IntelliJ IDEA.
 * User: Maxim.Mossienko
 * Date: Mar 23, 2004
 * Time: 6:37:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExprTypePredicate extends Handler {
  private RegExpPredicate delegate;
  private boolean withinHierarchy;

  public ExprTypePredicate(String type, String baseName, boolean _withinHierarchy, boolean caseSensitiveMatch,boolean target) {
    delegate = new RegExpPredicate(type,caseSensitiveMatch,baseName,false,target);
    withinHierarchy = _withinHierarchy;
  }

  public boolean match(PsiElement node, PsiElement match, MatchContext context) {
    if (match instanceof PsiIdentifier) {
      // since we pickup tokens
      match = match.getParent();
    }

    if (match instanceof PsiExpression) {
      final PsiType type = evalType((PsiExpression)match,context);
      if (type==null) return false;

      return doMatchWithTheType(type, context);
    } else {
      return false;
    }
  }

  protected PsiType evalType(PsiExpression match, MatchContext context) {
    PsiType type = null;

    if (match instanceof PsiReferenceExpression &&
        match.getParent() instanceof PsiMethodCallExpression) {
      PsiMethod method = ((PsiMethodCallExpression)match.getParent()).resolveMethod();
      if (method!=null) type = method.getReturnType();
    }

    if (type==null) type = match.getType();
    return type;
  }

  private boolean doMatchWithTheType(final PsiType type, MatchContext context) {
    if (type instanceof PsiClassType) {
      PsiClass clazz = ((PsiClassType)type).resolve();

      if (clazz!=null) return checkClass(clazz, context);
    }

    if (type!=null) {
      final String presentableText = type.getPresentableText();
      boolean result = delegate.doMatch(presentableText,context);

      if (!result && type instanceof PsiArrayType && ((PsiArrayType)type).getComponentType() instanceof PsiClassType) {
        PsiClass clazz = ((PsiClassType)((PsiArrayType)type).getComponentType()).resolve();

        if (clazz!=null) { // presentable text for array is not qualified!
          result = delegate.doMatch(clazz.getQualifiedName()+"[]",context);
        }
      }
      return result;
    } else {
      return false;
    }
  }

  public boolean checkClass(PsiClass clazz, MatchContext context) {
    if (withinHierarchy) {
      final NodeIterator parents = new HierarchyNodeIterator(clazz,true,true);

      while(parents.hasNext() && !delegate.match(null,parents.current(),context)) {
        parents.advance();
      }

      return parents.hasNext();
    } else {
      return delegate.match(null,clazz,context);
    }
  }
}
