package com.vrihas.assignment.pratilipi.pratilipiapp.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vrihas.assignment.pratilipi.pratilipiapp.R
import com.vrihas.assignment.pratilipi.pratilipiapp.data.model.User

/*
    Adapter class to handle the state of contacts
*/

class ContactsAdapter internal constructor(
    private var context: Context, private var contactAdapterCallback: ContactAdapterCallback
) : RecyclerView.Adapter<ContactsAdapter.MyViewHolder>(){

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var contactList = emptyList<User>() // Cached copy of contacts

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvContactName: TextView = itemView.findViewById(R.id.tv_contact_name)
        val tvContactNumber: TextView = itemView.findViewById(R.id.tv_contact_number)
        val tvAlphabet: TextView = itemView.findViewById(R.id.tv_alphabet)
        val ivBlock: ImageView = itemView.findViewById(R.id.iv_block)
        val rlContact: RelativeLayout = itemView.findViewById(R.id.rl_contact)
    }

    internal fun setContactList(contactList: MutableList<User>) {
        this.contactList = contactList
        notifyDataSetChanged()
//        Log.e("list", "list size ==> " + contactList.size )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = inflater.inflate(R.layout.contact_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount() = contactList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val contact = contactList[position]
        if (contact.name != null) {
            holder.tvAlphabet.text = contact.name[0].toString()
        }
        holder.tvContactName.text = contact.name
        holder.tvContactNumber.text = contact.number
        if (contact.blocked) {
            holder.ivBlock.visibility = View.VISIBLE
            holder.tvAlphabet.setTextColor(ContextCompat.getColor(context, R.color.colorRed))
        } else {
            holder.ivBlock.visibility = View.GONE
            holder.tvAlphabet.setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
        }
        holder.rlContact.setOnClickListener {
            val blockState: Boolean = !contact.blocked
            val user = User(contact.id, contact.name, contact.number, blockState)
            contactAdapterCallback.updateContactStatus(user)
        }
    }

    /*
        Interface to block the selected contact
     */
    interface ContactAdapterCallback {
        fun updateContactStatus(user: User)
    }
}