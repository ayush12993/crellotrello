package com.example.cresllo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cresllo.R
import com.example.cresllo.modals.Boards
import kotlinx.android.synthetic.main.activity_board.view.*
import kotlinx.android.synthetic.main.activity_profile_.*
import kotlinx.android.synthetic.main.item_board.view.*
import kotlinx.android.synthetic.main.activity_board.view.iv_board_image as iv_board_image1

open class BoardItemsAdapter (private val context: Context,
                              private var list: ArrayList<Boards> ) :
RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var onClickListener: OnClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
       return MyViewHolder(
           LayoutInflater
               .from(context).inflate(R.layout.item_board,
                   parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
       val model = list[position]
        if (holder is MyViewHolder){
            val into = Glide
                    .with(context)
                    .load(model.image)
                    .centerCrop()
                    .placeholder(R.drawable.ic_profile)
                    .into(holder.itemView.iv_board_image);

            holder.itemView.tvasname.text = model.name
            holder.itemView.tv_created_by.text = "Created By:${model.createdBy}"
            holder.itemView.setOnClickListener {
            if (onClickListener != null){
                onClickListener!!.onClick(position, model)
            }
            }
        }
    }

    interface OnClickListener{
        fun onClick(position: Int,model: Boards)
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}