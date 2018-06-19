package qq1296821114.androidsocketclient;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainRecyAdapter extends RecyclerView.Adapter<MainRecyAdapter.ViewHoder>{


    private int resourceId;
    private ArrayList<String> data;



    public MainRecyAdapter(int resourceId, ArrayList<String> list)
    {
        this.resourceId=resourceId;
        data=  list;
    }
    @Override
    public ViewHoder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_item, parent, false);
        ViewHoder viewHoder=new ViewHoder(view);
        return viewHoder;
    }

    @Override
    public void onBindViewHolder(ViewHoder holder, int position) {
        holder.textView.setText(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHoder extends RecyclerView.ViewHolder {
        protected TextView textView;
        public ViewHoder(View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.item_text);
        }
    }
}
