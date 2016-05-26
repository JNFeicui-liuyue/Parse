package contacts.feicui.edu.parse;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int WHAT = 1;

    private String jsonUrl = "http://192.168.1.147:8080/index2.jsp";

    private static final String TAG = "MainActivity";

    private ArrayList<ItemBean> mDatas;

    ListView lvList;
    Button get;
    String name;
    int age;

   public Handler  handler = new Handler(){
       @Override
       public void handleMessage(Message msg) {
           switch (msg.what){
                case WHAT:
                    lvList = (ListView) findViewById(R.id.lv_list);
                    lvList.setAdapter(new MyAdapter());
                    break;
            }


       }
   };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        get = (Button) findViewById(R.id.btn_get);

        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    @Override
                    public void run() {
                        getData();
//                        Looper.prepare();
                        handler.sendEmptyMessage(WHAT);
//                        handler = new Handler(){
//                            @Override
//                            public void handleMessage(Message msg) {
//                                switch (msg.what){
//                                    case WHAT:
//                                        tvName.setText(name);
//                                        tvAge.setText(String.valueOf(age));
//                                        break;
//                                }
//
//                            }
//                        };
//                        Looper.loop();
                    }
                }.start();

            }
        });
    }

    private void getData(){
        String jsonContext;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(jsonUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {

                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return;
            }
            jsonContext = buffer.toString();
            Log.d(TAG,"jsonContext:" + jsonContext);

            JSONObject jsonObject = new JSONObject(jsonContext);
            JSONArray jsonInfo = jsonObject.getJSONArray("students");
            for (int i = 0; i < jsonInfo.length(); i++) {


                JSONObject object = jsonInfo.getJSONObject(i);

//                name = object.getString("name");
//                age = object.getInt("age");

                mDatas = new ArrayList<>();

                ItemBean bean = new ItemBean();
                bean.name = object.getString("name");
                bean.age = object.getInt("age");

                mDatas.add(bean);
                Log.d(TAG,"mDatas"+mDatas);
                Log.d(TAG,"name:" + name + "age:"+age);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            if (mDatas != null) {
                return mDatas.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (mDatas != null) {
                return mDatas.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(MainActivity.this, R.layout.item_list, null);
            }
            TextView tvName = (TextView) convertView.findViewById(R.id.tv_name);
            TextView tvAge = (TextView) convertView.findViewById(R.id.tv_age);

            ItemBean Bean = mDatas.get(position);
            tvName.setText(Bean.name);
            tvAge.setText(Bean.age);

            return convertView;
        }
    }
}
