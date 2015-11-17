package com.project.estevao.chatbluetooth;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.project.estevao.chatbluetooth.entities.DataMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by c1284520 on 13/11/2015.
 */
public class ChatAdapter extends BaseAdapter {

    private final String read = "READ";
    private final String write = "WRITE";
    TextView txtViewMensagem;
    List<String> mensagem;
    List<DataMessage> dataMessages;
    private Activity context;
    View viewMsg;
    TextView textTyping;


    public ChatAdapter(Activity context) {
        this.context = context;
        dataMessages = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return dataMessages.size();
    }

    @Override
    public DataMessage getItem(int i) {
        return dataMessages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        int tipoTrue = 0;
        int tipoFalse = 0;
        DataMessage dataMessage = getItem(i);
        String txt = dataMessage.getTxt();

        viewMsg = context.getLayoutInflater().inflate(R.layout.message, viewGroup, false);
        CardView cardView = (CardView) viewMsg.findViewById(R.id.card_view);
        RelativeLayout layout = (RelativeLayout) viewMsg.findViewById(R.id.layout);
        TextView txtName = (TextView) viewMsg.findViewById(R.id.textName);

        ImageView image = (ImageView) viewMsg.findViewById(R.id.textImageMessage);

        TextView time = (TextView) viewMsg.findViewById(R.id.textTime);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

        time.setText(simpleDateFormat.format(dataMessage.getTime()));

        txtViewMensagem = (TextView) viewMsg.findViewById(R.id.textMessage);
        txtViewMensagem.setText(txt);


        if (dataMessage.getType().equals("READ")) {
            tipoTrue++;
            tipoFalse = 0;
            layout.setBackgroundColor(Color.parseColor("#C5E1A5"));
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) cardView.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            cardView.setLayoutParams(params);
            txtName.setVisibility(View.GONE);
            if (tipoTrue <= 1) {
                txtName.setVisibility(View.VISIBLE);
                txtName.setText(dataMessage.getNameUser());
            }
        } else if (dataMessage.getType().equals("WRITE")){
            tipoFalse++;
            tipoTrue = 0;
            layout.setBackgroundColor(Color.parseColor("#BBDEFB"));
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) cardView.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            cardView.setLayoutParams(params);
            txtName.setVisibility(View.GONE);
            if (tipoFalse <= 1) {
                txtName.setVisibility(View.VISIBLE);
                txtName.setText(dataMessage.getNameUser());
            }
        }
        else{
            tipoFalse++;
            tipoTrue = 0;
            layout.setBackgroundColor(Color.parseColor("#BBDEFB"));
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) cardView.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            cardView.setLayoutParams(params);
            txtName.setVisibility(View.GONE);
            image.setImageBitmap(StringToBitMap(dataMessage.getTxt()));

            if (tipoFalse <= 1) {
                txtName.setVisibility(View.VISIBLE);
                txtName.setText(dataMessage.getNameUser());
            }
        }

        return viewMsg;
    }

    public Bitmap StringToBitMap(String encodedString) {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
    }
    public void add(DataMessage data) {
        dataMessages.add(data);
    }
}
