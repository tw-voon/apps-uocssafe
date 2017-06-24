package app.uocssafe.com.uocs_safe.Emergency_Contact;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.transition.Visibility;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

import app.uocssafe.com.uocs_safe.Message.MessageAdapter;
import app.uocssafe.com.uocs_safe.R;

public class contactAdapter extends RecyclerView.Adapter<contactAdapter.MyViewHolder>{

    ArrayList<Contact> contactList = new ArrayList<>();
    public  Context context;
    private MessageAdapter messageAdapter;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, contact_no, desc;
        public final ImageButton callButton;
        public TextView readmore;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            contact_no = (TextView) itemView.findViewById(R.id.contact);
            desc = (TextView) itemView.findViewById(R.id.desc);
            callButton = (ImageButton) itemView.findViewById(R.id.call);
            readmore = (TextView) itemView.findViewById(R.id.readmore);
        }
    }
    contactAdapter (ArrayList<Contact> contactList)
    {
        this.contactList = contactList;
    }

    @Override
    public contactAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(itemView);
        return viewHolder;
    }

    public contactAdapter(Context context, ArrayList<Contact> contactList){
        this.contactList = contactList;
    }

    @Override
    public void onBindViewHolder(final contactAdapter.MyViewHolder holder, final int position) {
        final Contact contact = contactList.get(position);
        holder.title.setText(contact.getTitle());
        holder.contact_no.setText(contact.getContact_no());
        holder.desc.setText(contact.getDesc());
        holder.callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Contact contact = contactList.get(position);
                Intent i = new Intent(Intent.ACTION_DIAL);
                String phone = "tel:" + contact.getContact_no();
                i.setData(Uri.parse(phone));
                view.getContext().startActivity(i);
                Toast.makeText(view.getContext(), "Call to " + contact.getContact_no(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.readmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.desc.getVisibility() == View.GONE)
                    holder.desc.setVisibility(View.VISIBLE);
                else if(holder.desc.getVisibility() == View.VISIBLE)
                    holder.desc.setVisibility(View.GONE);
            }
        });

        ObjectAnimator animation = ObjectAnimator.ofInt(holder.desc, "maxLines", holder.desc.getMaxLines());
        animation.setDuration(200).start();
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public void filter(ArrayList<Contact> filterList)
    {
        contactList = new ArrayList<>();
        contactList.addAll(filterList);
        notifyDataSetChanged();
    }

}
