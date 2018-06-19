package qq1296821114.androidsocketclient;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_send;
    private EditText et_send,ip,so;
    private RecyclerView recyclerView;
    private MainRecyAdapter recyAdapter;
    private Toolbar toolbar;
    private ArrayList<String> data = new ArrayList<>();
    private Socket socket = null;
    private String TAG = "main";
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    String socket = msg.obj.toString();
                    data.add("服务器说：" + socket);
                    recyAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init_view();
        con_socket();
    }

    private void init_view() {
        btn_send = findViewById(R.id.btn_send);
        btn_send.setOnClickListener(this);

        et_send = findViewById(R.id.ed_send);
        recyclerView = findViewById(R.id.main_recycler);
        recyAdapter = new MainRecyAdapter(R.layout.recycle_item, data);

        toolbar=findViewById(R.id.toolbar2);
        so=toolbar.findViewById(R.id.main_so);
        ip=toolbar.findViewById(R.id.main_ip);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyAdapter);


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                if (socket == null || socket.isClosed()) {
                    con_socket();
                }
                final String tmp = et_send.getText().toString();
                if (!tmp.isEmpty()) {
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            PrintWriter wirter = null;
                            try {
                                wirter = new PrintWriter(new OutputStreamWriter(new DataOutputStream(socket.getOutputStream()),"UTF-8"));
                                wirter.write(tmp);
                                wirter.flush();
                                socket.shutdownOutput();
                                read(socket);
                                con_socket();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                    data.add(tmp);
                    recyAdapter.notifyDataSetChanged();
                    Log.e(TAG, "onClick: " + "i am click");
                }

                break;
            default:
                break;
        }
    }

    public void con_socket() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Log.e(TAG, "run: "+ ip.getText().toString()+Integer.parseInt(so.getText().toString()));
                    socket = new Socket(ip.getText().toString(),Integer.parseInt(so.getText().toString()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    public void read(Socket socket) {
        try {
//            OutputStream os = socket.getOutputStream();//字节输出流
//            PrintWriter pw = new PrintWriter(os);//将输出流包装成打印流
//            pw.flush();
//            socket.shutdownOutput();
            InputStream is = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String info = null;
            StringBuilder result = new StringBuilder("");
            while ((info = br.readLine()) != null) {
                result.append(info);
            }
            Message message=new Message();
            message.what=1;
            message.obj=result.toString();
            handler.sendMessage(message);
            br.close();
            is.close();
//            pw.close();
//            os.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
