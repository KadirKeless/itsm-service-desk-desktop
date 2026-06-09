package org.example.utils;

import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.JTextComponent;

/** JTextField / JTextArea için maksimum uzunluk (yapıştırma dahil). */
public final class TextDocumentLimit {

    private TextDocumentLimit() {}

    public static void apply(JTextComponent c, int maxLen) {
        if (!(c.getDocument() instanceof AbstractDocument)) return;
        ((AbstractDocument) c.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                int docLen = fb.getDocument().getLength();
                int ins = text == null ? 0 : text.length();
                int newLen = docLen - length + ins;
                if (newLen > maxLen) {
                    int allowed = maxLen - (docLen - length);
                    if (allowed <= 0) return;
                    text = text.substring(0, allowed);
                }
                super.replace(fb, offset, length, text, attrs);
            }
        });
    }
}
