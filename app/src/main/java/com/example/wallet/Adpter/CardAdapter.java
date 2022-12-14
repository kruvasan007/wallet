package com.example.wallet.Adpter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wallet.Model.Card;
import com.example.wallet.R;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {
    private ArrayList<Card> cardList;

    protected static View lastTransport;
    private int snapPosition = RecyclerView.NO_POSITION;
    private int lastPosition = -1;

    public CardAdapter(Context context, ArrayList<Card> card) {
        this.cardList = card;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardAdapter.ViewHolder holder, int position) {
        holder.bind(cardList.get(position));
    }

    public void setSnapPosition(int snapPosition) {
        this.lastPosition = this.snapPosition;
        this.snapPosition = snapPosition;
    }

    public int getLastSnapPosition() {
        return lastPosition;
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameView;
        private final ImageView barcode;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.name);
            barcode = itemView.findViewById(R.id.barcode);
        }

        public void bind(Card item) {
            nameView.setText(item.getNameCard());
            try {
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.encodeBitmap(item.getBarcode(), BarcodeFormat.CODE_128, 200, 40);
                barcode.setImageBitmap(bitmap);
            } catch (Exception e) {
                Log.e("E", "Error create barcode" + e.getMessage());
            }
        }
    }
}
