package com.example.android1finalproject.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android1finalproject.R;
import com.example.android1finalproject.adapter.ChatAdapter;
import com.example.android1finalproject.models.Message;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Chat#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Chat extends Fragment {
    public static String lastFragmentTag;

    private EditText messageEditText;
    private Button sendButton;
    private RecyclerView recyclerView;
    private ArrayList<Message> messageList;
    private ChatAdapter adapter;
    private FirebaseFirestore database;
    private FirebaseAuth auth;
    public static String gamename;
    public static String friendId;
    public static String senderName;
    public static String reciverName;
    Integer counter;

    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Chat() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Chat.
     */
    // TODO: Rename and change types and number of parameters
    public static Chat newInstance(String param1, String param2) {
        Chat fragment = new Chat();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        counter = 0;
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        messageEditText = view.findViewById(R.id.messageEditText);
        sendButton = view.findViewById(R.id.sendButton);
        recyclerView =(RecyclerView) view.findViewById(R.id.recyclerViewChat);
        database = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        messageList = new ArrayList<>();
        adapter = new ChatAdapter(getContext(),messageList,auth.getCurrentUser().getUid());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        showMessages();
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(messageEditText.getText().toString());
                messageEditText.setText("");
            }
        });
        recyclerView.setAdapter(adapter);

        return view;
    }
    private void showMessages()
    {
        if(lastFragmentTag.equals("UserList"))
        {
            CollectionReference oldMessagesRef = database.collection("games").document(gamename).collection("Likes").document(friendId)
                    .collection("chat with "+auth.getCurrentUser().getUid());
            Query query = oldMessagesRef.orderBy("timestamp", Query.Direction.ASCENDING);
            query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for(QueryDocumentSnapshot doc :queryDocumentSnapshots)
                    {
                        messageList.add(new Message(doc.getString("messageText")
                                ,doc.getString("senderId")
                                ,doc.getString("receiverId")
                                , FieldValue.serverTimestamp()
                                ,doc.getString("senderName")
                                ,doc.getString("reciverName")
                        ));
                    }
                }
            });
        }
        else if (lastFragmentTag.equals("SearchUsers")||lastFragmentTag.equals("Friends")) {
            CollectionReference oldMessagesRef = database.collection("users").document(auth.getUid())
                    .collection("friends").document(friendId).collection("chat");
            Query query = oldMessagesRef.orderBy("timestamp",Query.Direction.ASCENDING);
            query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for(QueryDocumentSnapshot doc :queryDocumentSnapshots)
                    {
                        messageList.add(new Message(doc.getString("messageText")
                                ,doc.getString("senderId")
                                ,doc.getString("receiverId")
                                , FieldValue.serverTimestamp()
                                ,doc.getString("senderName")
                                ,doc.getString("reciverName")
                        ));
                    }
                }
            });
        }
    }
    private void sendMessage(String messageText) {


        if (gamename != null && !gamename.isEmpty() && friendId != null && !friendId.isEmpty())
        {
            CollectionReference messagesFriendRef = database.collection("games")
                    .document(gamename)
                    .collection("Likes")
                    .document(friendId)
                    .collection("chat with " + auth.getUid());

            CollectionReference messagesMeRef = database.collection("games")
                    .document(gamename)
                    .collection("Likes")
                    .document(auth.getUid())
                    .collection("chat with "+friendId);


            // Create a new message document with a unique ID
            DocumentReference newMessageFriendRef = messagesFriendRef.document();
            DocumentReference newMessageMeRef = messagesMeRef.document();
            counter=counter+1;
            // Create a message object with sender, receiver, text, and timestamp
            Message messageFriend = new Message(messageText, auth.getUid(), friendId , FieldValue.serverTimestamp(),senderName,reciverName);
            Message messageMe = new Message(messageText,auth.getUid(),friendId,FieldValue.serverTimestamp(),senderName,reciverName);

            newMessageFriendRef.set(messageFriend)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            messagesFriendRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                    Query query = messagesFriendRef.orderBy("timestamp",Query.Direction.ASCENDING);
                                    query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            messageList.clear();
                                            for(QueryDocumentSnapshot doc :queryDocumentSnapshots)
                                            {

                                                messageList.add(new Message(doc.getString("messageText")
                                                        ,doc.getString("senderId")
                                                        ,doc.getString("receiverId")
                                                        , FieldValue.serverTimestamp()
                                                        ,doc.getString("senderName")
                                                        ,doc.getString("reciverName")
                                                ));
                                            }
                                        }
                                    });
                                }
                            });
                            Toast.makeText(requireContext(), getString(R.string.message_sent), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

            newMessageMeRef.set(messageMe)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else if(friendId != null && !friendId.isEmpty())
        {
            CollectionReference myFriendsRef = database.collection("users")
                    .document(auth.getUid()).collection("friends")
                    .document(friendId).collection("chat");
            CollectionReference hisFriendsRef = database.collection("users")
                    .document(friendId).collection("friends")
                    .document(auth.getUid()).collection("chat");

            DocumentReference newFriend = myFriendsRef.document();
            DocumentReference newMe = hisFriendsRef.document();

            Message messageFriend = new Message(messageText, auth.getUid(), friendId , FieldValue.serverTimestamp(),senderName,reciverName);
            Message messageMe = new Message(messageText,auth.getUid(),friendId,FieldValue.serverTimestamp(),senderName,reciverName);

            newFriend.set(messageFriend)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            myFriendsRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                    Query query = myFriendsRef.orderBy("timestamp",Query.Direction.ASCENDING);
                                    query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            messageList.clear();
                                            for(QueryDocumentSnapshot doc :queryDocumentSnapshots)
                                            {

                                                messageList.add(new Message(doc.getString("messageText")
                                                        ,doc.getString("senderId")
                                                        ,doc.getString("receiverId")
                                                        , FieldValue.serverTimestamp()
                                                        ,doc.getString("senderName")
                                                        ,doc.getString("reciverName")
                                                ));
                                            }
                                        }
                                    });
                                }
                            });
                            Toast.makeText(requireContext(), getString(R.string.message_sent), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

            newMe.set(messageMe)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.logout,menu);
        menu.findItem(R.id.other_action).setTitle(getString(R.string.main_screen));
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(auth.getCurrentUser()!=null)
        {
            switch (item.getItemId()){
                case R.id.logout_action:
                {
                    auth.signOut();
                    NavHostFragment.findNavController(this).navigate(R.id.action_chat_to_login);
                    break;
                }
                case R.id.requests_action:
                {
                    NavHostFragment.findNavController(this).navigate(R.id.action_chat_to_requests);
                    break;
                }
                case R.id.favorites_action:
                {
                    NavHostFragment.findNavController(this).navigate(R.id.action_chat_to_favoritesGames);
                    break;
                }
                case R.id.friends_action:
                {
                    NavHostFragment.findNavController(this).navigate(R.id.action_chat_to_friends2);
                    break;
                }
                case R.id.search_users:
                {
                    NavHostFragment.findNavController(this).navigate(R.id.action_chat_to_filterFriends);
                    break;
                }
                case R.id.other_action:
                {
                    NavHostFragment.findNavController(this).navigate(R.id.action_chat_to_category);
                    break;
                }
            }
        }
        return true;
    }
}