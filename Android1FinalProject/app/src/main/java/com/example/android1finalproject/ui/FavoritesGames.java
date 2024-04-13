package com.example.android1finalproject.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
import com.example.android1finalproject.models.Games;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavoritesGames#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoritesGames extends Fragment implements MainScreenAdapter.OnItemClickListener{
    private RecyclerView recyclerView ;
    private FirebaseFirestore database;
    private FirebaseAuth auth;
    private LinearLayoutManager layoutManager;
    private MainScreenAdapter adapter;
    private ArrayList<Games> arrGames;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FavoritesGames() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FavoritesGames.
     */
    // TODO: Rename and change types and number of parameters
    public static FavoritesGames newInstance(String param1, String param2) {
        FavoritesGames fragment = new FavoritesGames();
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
        View v = inflater.inflate(R.layout.fragment_favorites_games, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_favorites_games);
        layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        arrGames = new ArrayList<>();
        Toast.makeText(requireContext(), getString(R.string.tap_an_item_to_get_the_description_slide_to_unlike), Toast.LENGTH_SHORT).show();


        database.collection("users").document(auth.getUid()).collection("Likes").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot doc : docs) {
                            String name = doc.getString("game");
                            String added = doc.getString("added");
                            String image = doc.getString("image");
                            ArrayList<String> genres = (ArrayList<String>) doc.get("genres");
                            ArrayList<String> platforms = (ArrayList<String>) doc.get("platform");
                            arrGames.add(new Games(name, added, genres, platforms, image));
                        }

                        adapter = new MainScreenAdapter(arrGames);
                        adapter.setOnClickListener(FavoritesGames.this);
                        recyclerView.setAdapter(adapter);
                        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
                        itemTouchHelper.attachToRecyclerView(recyclerView);
                    }
                });

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
            Games game = arrGames.get(position);
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
                                Toast.makeText(requireContext(), R.string.unlike, Toast.LENGTH_SHORT).show();
                                arrGames.remove(position);
                                recyclerView.getAdapter().notifyDataSetChanged();
                                recyclerView.getAdapter().notifyItemRemoved(position);
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

        }


    };

    @Override
    public void onItemClick(int position) {
        Games clickedGame = arrGames.get(position);

        Bundle bundle = new Bundle();
        bundle.putString("name" , clickedGame.getName());
        bundle.putString("downloads" , clickedGame.getAdded());
        bundle.putString("image", clickedGame.getImage());
        bundle.putStringArrayList("genres", clickedGame.getGenres());
        bundle.putStringArrayList("platforms", clickedGame.getPlatform());
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_favoritesGames_to_gameDescription , bundle);
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.logout,menu);
        menu.findItem(R.id.favorites_action).setTitle(getString(R.string.main_screen));
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
                    NavHostFragment.findNavController(this).navigate(R.id.action_favoritesGames_to_login);
                    break;
                }
                case R.id.requests_action:
                {
                    NavHostFragment.findNavController(this).navigate(R.id.action_favoritesGames_to_requests);
                    break;
                }
                case R.id.favorites_action:
                {
                    NavHostFragment.findNavController(this).navigate(R.id.action_favoritesGames_to_category);
                    break;
                }
                case R.id.friends_action:
                {
                    NavHostFragment.findNavController(this).navigate(R.id.action_favoritesGames_to_friends2);
                    break;
                }
                case R.id.search_users:
                {
                    NavHostFragment.findNavController(this).navigate(R.id.action_favoritesGames_to_filterFriends);
                    break;
                }
            }
        }
        return true;
    }
}