package com.example.cresllo.adapters

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cresllo.R
import com.example.cresllo.modals.Users
import com.example.cresllo.utils.Constants
import kotlinx.android.synthetic.main.activity_profile_.*

import kotlinx.android.synthetic.main.item_card.view.*
import kotlinx.android.synthetic.main.item_member.view.*

open class MemberListItemsAdapter (
    private val context: Context,
    private var list: ArrayList<Users>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
                LayoutInflater.from(context).inflate(
                        R.layout.item_member,
                        parent,
                        false
                )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
      val model = list[position]

        if (holder is MyViewHolder){

            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_profile)
                .into(holder.itemView.iv_member_image);

            holder.itemView.tv_member_name.text = model.name
            holder.itemView.tv_member_email.text = model.email

             if(model.selected){
              holder.itemView.iv_selected_member.visibility = View.VISIBLE
             }else{
                 holder.itemView.iv_selected_member.visibility = View.GONE
             }

            holder.itemView.setOnClickListener {
                if (onClickListener != null){
                    if (model.selected){
                        onClickListener!!.onClick(position,model,Constants.UNSELECT)
                    }else{
                        onClickListener!!.onClick(position,model,Constants.SELECT)
                    }
                }
            }
        }
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    interface OnClickListener{
        fun onClick(position: Int, users: Users, action: String)
    }

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view)

}