package com.example.android1finalproject.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android1finalproject.R;
import com.example.android1finalproject.adapter.MainScreenAdapter;
import com.example.android1finalproject.data.DataService;
import com.example.android1finalproject.models.Friend;
import com.example.android1finalproject.models.Games;
import com.example.android1finalproject.models.Likes;
import com.example.android1finalproject.models.Request;
import com.example.android1finalproject.models.SearchUsersModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainScreen#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainScreen extends Fragment implements MainScreenAdapter.OnItemClickListener {

    private RecyclerView recyclerView ;
    private FirebaseFirestore database;
    private FirebaseAuth auth;
    private LinearLayoutManager layoutManager;
    private MainScreenAdapter mainScreenAdapter;
    private ArrayList<Games> arrCat;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MainScreen() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Category.
     */
    // TODO: Rename and change types and number of parameters
    public static MainScreen newInstance(String param1, String param2) {
        MainScreen fragment = new MainScreen();
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



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_games, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler);
        layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        arrCat = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        database.collection("games").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot doc :queryDocumentSnapshots.getDocuments())
                {
                    arrCat.add(new Games(doc.getString("name"),doc.getString("added"),
                            (ArrayList<String>) doc.get("genres"),(ArrayList<String>)doc.get("platform")
                            ,doc.getString("image")));
                }
                mainScreenAdapter = new MainScreenAdapter(arrCat);
                mainScreenAdapter.setOnClickListener(MainScreen.this);
                recyclerView.setAdapter(mainScreenAdapter);
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
                itemTouchHelper.attachToRecyclerView(recyclerView);
            }
        });
        Toast.makeText(requireContext(),getString(R.string.main_screen_start) , Toast.LENGTH_SHORT).show();



        return v;
    }
    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(
            0, // no drag-and-drop
            ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
                            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                                int position = viewHolder.getAdapterPosition();
                                HashMap<String,Object> like = new HashMap<>();
                                Games game = arrCat.get(position);
                                database.collection("games").document(game.getName()).collection("Likes").get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                ArrayList<String> uidList = new ArrayList<>();
                                                for(DocumentSnapshot doc :queryDocumentSnapshots.getDocuments())
                                                {
                                                    uidList.add(doc.getId());
                                                }
                                                if(uidList.contains(auth.getUid()))
                                                {
                                                    database.collection("games").document(game.getName()).collection("Likes")
                                                            .document(auth.getUid()).delete();
                                                    database.collection("users").document(auth.getUid()).collection("Likes")
                                                            .document(game.getName()).delete();
                                                    Toast.makeText(requireContext(),getString(R.string.unlike) , Toast.LENGTH_SHORT).show();
                                                }
                                                else
                                                {
                                                    database.collection("users").document(auth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                            like.put("username",documentSnapshot.getString("username"));
                                                            like.put("game",game.getName());
                                                            like.put("Timestamp",FieldValue.serverTimestamp());
                                                            like.put("userId",auth.getUid());
                                                            like.put("isLiked",true);
                                                            like.put("image",game.getImage());
                                                            like.put("added",game.getAdded());
                                                            like.put("genres",game.getGenres());
                                                            like.put("platform",game.getPlatform());
                                                            database.collection("games").document(game.getName()).collection("Likes")
                                                                    .document(auth.getUid()).set(like);
                                                            database.collection("users").document(auth.getUid()).collection("Likes")
                                                                    .document(game.getName()).set(like);
                                                        }
                                                    });
                                                    Toast.makeText(requireContext(), getString(R.string.like), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
            recyclerView.getAdapter().notifyDataSetChanged();
            recyclerView.getAdapter().notifyItemRemoved(position);
        }


    };

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.logout,menu);
        menu.findItem(R.id.other_action).setVisible(false);
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
                    NavHostFragment.findNavController(this).navigate(R.id.action_category_to_login);
                    break;
                }
                case R.id.requests_action:
                {
                    NavHostFragment.findNavController(this).navigate(R.id.action_category_to_requests);
                    break;
                }
                case R.id.favorites_action:
                {
                    NavHostFragment.findNavController(this).navigate(R.id.action_category_to_favoritesGames);
                    break;
                }
                case R.id.friends_action:
                {
                    NavHostFragment.findNavController(this).navigate(R.id.action_category_to_friends2);
                    break;
                }
                case R.id.search_users:
                {
                    NavHostFragment.findNavController(this).navigate(R.id.action_category_to_filterFriends);
                    break;
                }
            }
        }
        return true;
    }

    @Override
    public void onItemClick(int position) {

        Games clickedGame = arrCat.get(position);


        Bundle bundle = new Bundle();
        bundle.putString("name" , clickedGame.getName());
        bundle.putString("downloads" , clickedGame.getAdded());
        bundle.putString("image", clickedGame.getImage());
        bundle.putStringArrayList("genres", clickedGame.getGenres());
        bundle.putStringArrayList("platforms", clickedGame.getPlatform());
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_category_to_gameDescription , bundle);
    }
}