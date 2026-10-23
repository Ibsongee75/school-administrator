package com.schooladministrator.app;

import android.content.Context;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "PrintPlugin")
public class PrintPlugin extends Plugin {

    private WebView printWebView;

    /* prints the page currently shown in the app */
    @PluginMethod
    public void print(final PluginCall call) {
        getActivity().runOnUiThread(() -> {
            try {
                WebView webView = getBridge().getWebView();
                String jobName = call.getString("name", "School Administrator");
                PrintManager pm = (PrintManager) getActivity().getSystemService(Context.PRINT_SERVICE);
                PrintDocumentAdapter adapter = webView.createPrintDocumentAdapter(jobName);
                pm.print(jobName, adapter, new PrintAttributes.Builder().build());
                call.resolve();
            } catch (Exception e) {
                call.reject(String.valueOf(e.getMessage()));
            }
        });
    }

    /* prints a block of HTML on its own (receipts, letters) */
    @PluginMethod
    public void printHtml(final PluginCall call) {
        final String html = call.getString("html", "");
        final String jobName = call.getString("name", "Document");
        getActivity().runOnUiThread(() -> {
            try {
                final boolean[] done = {false};
                final WebView wv = new WebView(getActivity());
                wv.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        if (done[0]) return;
                        done[0] = true;
                        PrintManager pm = (PrintManager) getActivity().getSystemService(Context.PRINT_SERVICE);
                        PrintDocumentAdapter adapter = view.createPrintDocumentAdapter(jobName);
                        pm.print(jobName, adapter, new PrintAttributes.Builder().build());
                    }
                });
                printWebView = wv;
                wv.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
                call.resolve();
            } catch (Exception e) {
                call.reject(String.valueOf(e.getMessage()));
            }
        });
    }
}
