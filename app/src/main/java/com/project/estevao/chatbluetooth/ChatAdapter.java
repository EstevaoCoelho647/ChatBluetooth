package com.project.estevao.chatbluetooth;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.text.Layout;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.project.estevao.chatbluetooth.entities.DataMessage;

import junit.framework.Test;

import org.w3c.dom.Text;

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

        DataMessage dataMessage = getItem(i);
        String txt = dataMessage.getTxt();

        viewMsg = context.getLayoutInflater().inflate(R.layout.message, viewGroup, false);
        CardView cardView = (CardView) viewMsg.findViewById(R.id.card_view);
        RelativeLayout layout = (RelativeLayout) viewMsg.findViewById(R.id.layout);
        TextView txtName = (TextView) viewMsg.findViewById(R.id.textName);

        txtViewMensagem = (TextView) viewMsg.findViewById(R.id.textMessage);
        txtViewMensagem.setText(txt);

        if (dataMessage.isType() == true){
            layout.setBackgroundColor(Color.parseColor("#C5E1A5"));
            txtName.setText(dataMessage.getNameUser());

        }
        else{
            layout.setBackgroundColor(Color.parseColor("#BBDEFB"));
            txtName.setText(dataMessage.getNameUser());

        }



        return viewMsg;
    }
    public void add(DataMessage data) {
        DataMessage dataMessage = new DataMessage();
        dataMessage.setTxt(data.getTxt());
        dataMessage.setType(data.getType());
        dataMessage.setNameUser(data.getNameUser());

        //add in array
        dataMessages.add(dataMessage);
    }
}
