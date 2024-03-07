package com.College.Vindhya_Group_Of_Institutions;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Progress_Dialog extends Dialog {

    private final ProgressBar progressBar;
    private TextView messageText;

    public Progress_Dialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.progress_dialog);

        this.progressBar = findViewById(R.id.customProgressBar);
        this.messageText = findViewById(R.id.customMessage);

        // Check for null before accessing methods
        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }

        // Set the dialog not to be canceled by touching outside
        setCanceledOnTouchOutside(false);
    }

    public void setMessage(String message) {
        messageText.setText(message);
    }
}
