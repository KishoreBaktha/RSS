package com.example.kishorebaktha.rss;
        import java.io.IOException;
        import java.io.InputStream;
        import java.net.MalformedURLException;
        import java.net.URL;
        import java.util.ArrayList;
        import java.util.List;
        import org.xmlpull.v1.XmlPullParser;
        import org.xmlpull.v1.XmlPullParserException;
        import org.xmlpull.v1.XmlPullParserFactory;
        import android.app.ListActivity;
        import android.content.Intent;
        import android.net.Uri;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.ArrayAdapter;
        import android.widget.ListView;

public class MainActivity extends ListActivity {
    List headlines, links;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        new DoSomeTask().execute();
    }
    // DoSomeTask class will do all the work on another thread so there will be
    // no
    // ANR and UI hanging.
    private class DoSomeTask extends AsyncTask<Void, Void, Void> {
        /*
        * (non-Javadoc)
        *
        * @see android.os.AsyncTask#doInBackground(Params[])
        */
        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                headlines = new ArrayList();
                links = new ArrayList();

               URL url = new URL("http://www.thehindubusinessline.com/?service=rss");
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
              // xpp.setInput(getResources().openRawResource(R.raw.customfeed), "UTF-8" );
                // We will get the XML from an input stream
              xpp.setInput(getInputStream(url), "UTF-8");
                boolean insideItem = false;
                // Returns the type of current event: START_TAG, END_TAG, etc..
                int eventType = xpp.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT)
                {
                    if (eventType == XmlPullParser.START_TAG)
                    {
                        if (xpp.getName().equalsIgnoreCase("item"))
                        {
                            insideItem = true;
                        }
                        else if (xpp.getName().equalsIgnoreCase("title"))
                        {
                            if(insideItem)
                                headlines.add(xpp.nextText()); // extract the
                            // headline
                        } else if (xpp.getName().equalsIgnoreCase("link")) {
                            if (insideItem)
                                links.add(xpp.nextText()); // extract the link
                            // of article
                        }
                    } else if (eventType == XmlPullParser.END_TAG
                            && xpp.getName().equalsIgnoreCase("item")) {
                        insideItem = false;
                    }

                    eventType = xpp.next(); // move to next element
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Void params) {
            try {
                // Binding data
                ArrayAdapter adapter = new ArrayAdapter(MainActivity.this,
                        android.R.layout.simple_list_item_1, headlines);

                setListAdapter(adapter);
            } catch (Exception e) {
            }

        }
    }

    public InputStream getInputStream(URL url) {
        try {
            return url.openConnection().getInputStream();
        } catch (IOException e) {
            return null;
        }
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
        Uri uri = Uri.parse((String) links.get(position));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}


