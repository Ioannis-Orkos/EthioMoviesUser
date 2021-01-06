package com.ioannisnicos.ethiomoviesuser.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ioannisnicos.ethiomoviesuser.R;
import com.ioannisnicos.ethiomoviesuser.models.Stores;
import com.ioannisnicos.ethiomoviesuser.models.StoresAdditionalStatus;
import com.ioannisnicos.ethiomoviesuser.retrofit.ApiClient;


import java.lang.ref.WeakReference;
import java.util.List;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class SubscriptionsRecyclerAdapter extends RecyclerView.Adapter<SubscriptionsRecyclerAdapter.MovieViewHolder> {

    private WeakReference<Context> mContext;
    private List<StoresAdditionalStatus> mStoresSubsStatus;
    private List<Stores> mStores;
    private OnSubscribeButtonClickListener mSubscribeButtonListener;
    private OnSubscriptionBKClickListener mSubscriptionBKClickListener;

    public SubscriptionsRecyclerAdapter(Context context, List<Stores> stores, List<StoresAdditionalStatus> storesSubs) {
        mContext = new WeakReference<Context>(context);
        mStores = stores;
        mStoresSubsStatus = storesSubs;
    }

    public void setOnSubscribeButtonClickListener(OnSubscribeButtonClickListener listener) {
        mSubscribeButtonListener = listener;
    }

    public void setOnSubscriptionBKClickListener(OnSubscriptionBKClickListener listener) {
        mSubscriptionBKClickListener = listener;
    }

    @Override
    public SubscriptionsRecyclerAdapter.MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SubscriptionsRecyclerAdapter.MovieViewHolder(LayoutInflater.from(mContext.get()).inflate(R.layout.card_subscriptions_large, parent, false));
    }

    @Override
    public void onBindViewHolder(SubscriptionsRecyclerAdapter.MovieViewHolder holder, int position) {

          Glide.with(mContext.get()).load(ApiClient.BASE_URL+mStores.get(position).getBanner())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.storeBusinessImageview);
          Glide.with(mContext.get()).load(ApiClient.BASE_URL+mStores.get(position).getProf_img())
                 .centerCrop()
                 .diskCacheStrategy(DiskCacheStrategy.ALL)
                  .into(holder.profilePictureImageview);
//                        /*    try {
//                                Date date1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(mStores.get(position).getReg_date());
//                                holder.displaynameText.setText(date1.toString());
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                            }*/
        holder.storeBusinessNameText.setText("Business Name - "+mStores.get(position).getBusiness_name());
        holder.storeUserNameText.setText("Name - "+mStores.get(position).getDisplay_name());
        holder.storeDescriptionText.setText("Description - "+mStores.get(position).getDescription());

        if( mStoresSubsStatus.get(position).getSubscription_status()==null)    {
            holder.subscribeButton.setText("Subscribe");
            holder.subscribeButton.setBackgroundResource(R.drawable.style_button_radius);          }
         else if(mStoresSubsStatus.get(position).getSubscription_status().equals("subscribed"))    {
            holder.subscribeButton.setText("Unsubscribe");
           }else  if(mStoresSubsStatus.get(position).getSubscription_status().equals("request"))    {
            holder.subscribeButton.setText("Cancel request");
       }

    }

    @Override
    public int getItemCount() {
        return mStores.size();
    }

    public interface OnSubscribeButtonClickListener {
        void onSubscribeButtonClick(int position);
    }

    public interface OnSubscriptionBKClickListener {
        void onSubscriptionBKClick(int position);
    }

        public class MovieViewHolder extends RecyclerView.ViewHolder {

        public TextView storeUserNameText;
        public TextView storeDescriptionText;
        public TextView storeBusinessNameText;
        public ImageView storeBusinessImageview;
        public CircleImageView profilePictureImageview;
        public Button subscribeButton;
        public ConstraintLayout cardBackGround;


        public MovieViewHolder(View itemView) {
            super(itemView);


            storeBusinessImageview = itemView.findViewById(R.id.imageView_subscription_storeimg);
            profilePictureImageview =  itemView.findViewById(R.id.circle_img_subscription_storeprof);
            storeBusinessNameText =  itemView.findViewById(R.id.textView_subscription_store_business_name);
            storeUserNameText = itemView.findViewById(R.id.textView_subscrription_store_user_name);
            storeDescriptionText =   itemView.findViewById(R.id.textView_subscription_store_description);
            subscribeButton = itemView.findViewById(R.id.button_subscription_subscribe);
            cardBackGround =  itemView.findViewById(R.id.card_subscriptions_large);

            cardBackGround.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       if (mSubscriptionBKClickListener != null) {
                           int position = getAdapterPosition();
                           if (position != RecyclerView.NO_POSITION) {
                               mSubscriptionBKClickListener.onSubscriptionBKClick(position);
                           }}
                   }
            }
            );


            subscribeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSubscribeButtonListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mSubscribeButtonListener.onSubscribeButtonClick(position);
                        }
                    }
                }
            });

        }
    }

}
