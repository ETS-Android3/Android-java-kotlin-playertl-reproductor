# RecyclerView

https://developer.android.com/guide/topics/ui/layout/recyclerview?hl=es-419#java

1.Metemos nuestro RecyclerView en el layout donde lo queramos mostrar:
    <?xml version="1.0" encoding="utf-8"?>
    <androidx.appcompat.widget.LinearLayoutCompat xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:app="http://schemas.android.com/apk/res-auto"
        xmlns:tools="http://schemas.android.com/tools"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        tools:context=".MainActivity"
        android:background="@color/teal_200"
        android:orientation="vertical">

        <androidx.recyclerview.widget.RecyclerView
            android:id="@+id/recyclerViewMainActivity"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            app:flow_verticalAlign="center"
            tools:layout_editor_absoluteX="0dp"
            tools:layout_editor_absoluteY="63dp" />
    </androidx.appcompat.widget.LinearLayoutCompat>

2.Creamos la vista que contendrá cada item del recyclerView

3.Creamos una carpeta y dentro creamos una clase cuyo nombre termine en RecyclerView con esta estructura:

    public class ListasCancionesRecyclerAdapter extends RecyclerView.Adapter<ListasCancionesRecyclerAdapter.ViewHolder> {
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(View view) {
                super(view);
            }
        }
    }