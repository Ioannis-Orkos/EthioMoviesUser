package com.ioannisnicos.ethiomoviesuser.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ioannisnicos.ethiomoviesuser.R;
import com.ioannisnicos.ethiomoviesuser.retrofit.ApiClient;

import com.ioannisnicos.ethiomoviesuser.retrofit_tvshow_response.TransactionTvshowResponse;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class TransactionsTvshowRecyclerAdapter extends RecyclerView.Adapter<TransactionsTvshowRecyclerAdapter.TransactionViewHolder> {

    private Context mContext;
    private List<TransactionTvshowResponse> mTransationList;

    private TransactionsMovieRecyclerAdapter.OnTransactionDelButtonClickListener mTransactionDelButtonListener;

    public interface OnTransactionDelButtonClickListener {
        void onTransactionDelAcceptButtonClick(int position);
    }

    public void setOnTransactionDelButtonClickListener(TransactionsMovieRecyclerAdapter.OnTransactionDelButtonClickListener listener) {
        mTransactionDelButtonListener = listener;
    }




    public TransactionsTvshowRecyclerAdapter(Context context, List<TransactionTvshowResponse> transations) {
        mContext = context;
        mTransationList = transations;
    }


    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TransactionsTvshowRecyclerAdapter.TransactionViewHolder(LayoutInflater.from(mContext).inflate(R.layout.card_transaction_movie, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull TransactionsTvshowRecyclerAdapter.TransactionViewHolder holder, int position) {

        holder.mProgressBar.setVisibility(View.GONE);
        Glide.with(mContext.getApplicationContext()).load(mTransationList.get(position).getTvshow().getPoster())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
               .placeholder(R.drawable.ic_movie_black_24)
               .into(holder.movieIV);
        Glide.with(mContext.getApplicationContext()).load(ApiClient.BASE_URL+mTransationList.get(position).getStores().getProf_img())
                .centerCrop()
                .placeholder(R.drawable.ic_movie_black_24)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.storeCV);
        Glide.with(mContext.getApplicationContext()).load(ApiClient.BASE_URL+mTransationList.get(position).getStores().getBanner())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_movie_black_24)
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        holder.cardBackGround.setBackground(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
        holder.movieTitleText.setText(mTransationList.get(position).getTvshow().getTitle());

        if(mTransationList.get(position).getStores().getBusiness_name().isEmpty() || mTransationList.get(position).getStores().getBusiness_name() ==null  )
            holder.storeNameText.setText(mTransationList.get(position).getStores().getDisplay_name());
        else holder.storeNameText.setText(mTransationList.get(position).getStores().getBusiness_name());

        holder.userMsgText.setText("User msg - "+mTransationList.get(position).getStatus().getReq_user_msg());
        holder.storeMsgText.setText("Store msg - "+mTransationList.get(position).getStatus().getReq_store_msg());


        if(mTransationList.get(position).getStatus().getReq_status().equals("Status - pending"))
            holder.statusText.setText("Status - pending action");
        else  holder.statusText.setText("Status - "+mTransationList.get(position).getStatus().getReq_status());



        /*    try {
            Date date1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(mStores.get(position).getReg_date());
            holder.displaynameText.setText(date1.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }*/
       /*

        if( mTransationList.get(position).getUser_relation_status()==null)    {
            holder.displaynameBut.setText("Follow");
            holder.displaynameBut.setBackgroundResource(R.drawable.btn_follow_style);          }
        else if(mTransationList.get(position).getUser_relation_status().equals("accepted"))    {
            holder.displaynameBut.setText("UnFollow");
        }else  if(mTransationList.get(position).getUser_relation_status().equals("request"))    {
            holder.displaynameBut.setText("Cancel Request");

        }*/

    }

    @Override
    public int getItemCount() {
        return mTransationList.size();
    }


    public class TransactionViewHolder extends RecyclerView.ViewHolder {


        public ImageView movieIV;
        public TextView movieTitleText;

        public CircleImageView storeCV;
        public TextView storeNameText;
        public ConstraintLayout cardBackGround;

        public TextView statusText;
        public TextView userMsgText;
        public TextView storeMsgText;

        public ImageView            delButtonImageView;
        public ProgressBar mProgressBar;


        public TransactionViewHolder(View itemView) {
            super(itemView);

            mProgressBar = itemView.findViewById(R.id.progressBar_transactionCard);
            movieIV = itemView.findViewById(R.id.imageView_movie_transactionCard);
            storeCV =  itemView.findViewById(R.id.circleimageview_transaction_store);

            movieTitleText = itemView.findViewById(R.id.textview_transaction_movietitle);
            storeNameText =   itemView.findViewById(R.id.textview_transaction_storename);
            cardBackGround =  itemView.findViewById(R.id.card_store_background);

            statusText = itemView.findViewById(R.id.textView_transactionCard_status);
            userMsgText = itemView.findViewById(R.id.textview_transaction_movieusermsg);
            storeMsgText = itemView.findViewById(R.id.textview_transaction_moviestoremsg);


            delButtonImageView = itemView.findViewById(R.id.imageButton_transactionCard_del);
            delButtonImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mTransactionDelButtonListener.onTransactionDelAcceptButtonClick(getAdapterPosition());
                }
            });
        }
    }



}