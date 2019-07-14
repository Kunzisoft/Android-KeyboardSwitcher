package com.kunzisoft.keyboard.switcher.settings;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;

import com.kunzisoft.keyboard.switcher.R;
import com.kunzisoft.keyboard.switcher.utils.Constants;

/**
 * Show the about page
 */
public class AboutFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getContext() != null) {
            View rootView = inflater.inflate(R.layout.about_activity, container, false);

            TextView versionTextView = rootView.findViewById(R.id.activity_about_version);
            String versionString = getString(R.string.about_version) + " " + Constants.getVersion(getContext());
            versionTextView.setText(versionString);

            String htmlContent =
                    "<p>" + getString(R.string.html_text_purpose) + "</p>" +

                    "<h2>" + getString(R.string.about_title) + "</h2>" +
                    "<p>" + getString(R.string.html_text_free, getString(R.string.app_name)) + "</p>" +
                    "<p>" + getString(R.string.html_text_contribution) + "</p>" +

                    "<h2>" + getString(R.string.contact_title) + "</h2>" +
                    "<p>" + getString(R.string.source_code) + " <a href=\"" + Constants.URL_CONTRIBUTION + "\">" + Constants.URL_CONTRIBUTION + "</a></p>" +
                    "<p>" + getString(R.string.powered_by) + " <a href=\"" + Constants.URL_WEB_SITE + "\">" + Constants.ORGANIZATION + "</a></p>";

            TextView aboutTextView = rootView.findViewById(R.id.activity_about_content);
            aboutTextView.setMovementMethod(LinkMovementMethod.getInstance());
            aboutTextView.setText(HtmlCompat.fromHtml(htmlContent, HtmlCompat.FROM_HTML_MODE_LEGACY));
            return rootView;
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
