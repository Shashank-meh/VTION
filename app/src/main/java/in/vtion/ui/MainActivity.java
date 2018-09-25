package in.vtion.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import in.vtion.db.DataSourceHandler;
import in.vtion.db.DbAppDataObject;
import in.vtion.sampleapp.R;

public class MainActivity extends Activity {

    List<DbAppDataObject> data = null;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.mainactivity);

        Button notificationBtn = (Button) findViewById(R.id.notificationBtn);
        if (Build.VERSION.SDK_INT >= 19) {
            notificationBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                    } catch (Exception e) {
                    }
                }
            });
        } else {
            notificationBtn.setVisibility(View.GONE);
        }

        DataSourceHandler db = DataSourceHandler.getInstance(this);
        data = db.getAllWhereAppDataObject();

        String vals[] = new String[data.size()];

        ListView view = findViewById(R.id.listView);
        final MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(this, vals);
        view.setAdapter(adapter);
    }

    class MySimpleArrayAdapter extends ArrayAdapter<String> {

        public MySimpleArrayAdapter(Context context, String[] values) {
            super(context, -1, values);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) this.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.rowlayout, parent, false);

            DbAppDataObject obj = data.get(position);

            TextView key = rowView.findViewById(R.id.key);
            key.setText(obj.key);

            TextView content = rowView.findViewById(R.id.content);
            if (obj.data == null || obj.data.length() <= 0)
                content.setVisibility(View.GONE);
            else
                content.setText(obj.data);

            TextView time = rowView.findViewById(R.id.time);
            time.setText(new Date(obj.timestamp).toString());

            return rowView;
        }
    }
}
