package dal;

import be.Category;
import be.Movie;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MovieDAO {
    private PreparedStatement preparedStatement;
    DBConnection dbConnection = new DBConnection();


    /**
     * retrieves all movies stored in db
     * @return list of all movies in db
     * @throws SQLException
     */
    public List<Movie> getAllMovies() throws SQLException {
        Movie movie;
        List<Movie> retrievedMovies = new ArrayList<>();
        try(Connection connection = dbConnection.getConnection()){
            String sql ="SELECT id, movie_title, user_rating, absolute_path, last_viewed, imdb_rating FROM Movie";
            preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                int id = rs.getInt("id");
                String name = rs.getString("movie_title");
                String rating = rs.getString("user_rating");
                String absolutePath = rs.getString("absolute_path");
                String lastViewed = rs.getString("last_viewed");
                String imdbRating = rs.getString("imdb_rating");
                movie = new Movie(id, name, rating, absolutePath, lastViewed, imdbRating);
                retrievedMovies.add(movie);
            }
        }
        return retrievedMovies;
    }

    /**
     * finds movie from db based on given id
     * @param id
     * @return movie based off given id
     * @throws SQLException
     */
    public Movie getMovieByID(int id) throws SQLException {
        List<Movie> movies = getAllMovies();
        for (Movie m: movies
        ) {
            if(m.getId() == id)
                return m;
        }
        return null;
    }

    /**
     * finds and deletes movie from db based on given id
     * @param id
     * @throws SQLException
     */
    public void deleteMovieByID(int id) throws SQLException {
        try(Connection connection = dbConnection.getConnection()){
            String sql = "DELETE FROM Movie WHERE(id = ?)";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
        }
    }

    /**
     * We get the id from the movie selected and deletes it from table CatMovie.
     * We need to do this before deleting a movie from table Movie, otherwise we will have issues with the keys.
     *
     * @param movie
     */
    public void removeMovieFromCategory(Movie movie) throws SQLException {
        int id = movie.getId();
        String sql = "DELETE FROM CatMovie WHERE movie_id='" + id + "';";
        try (Connection con = dbConnection.getConnection();) {
            con.createStatement().execute(sql);
        }
    }

    /**
     * adds movie to db based on given name, rating, absolutePath, and lastViewed
     * @param name
     * @param rating
     * @param absolutePath
     * @param lastViewed
     * @throws SQLException
     */
    public void addMovie(String name, String rating, String absolutePath, String lastViewed, String imdbRating) throws SQLException {
        try(Connection connection = dbConnection.getConnection()){
            String sql = "INSERT INTO Movie(movie_title, user_rating, absolute_path, last_viewed, imdb_rating) VALUES(?,?,?,?,?)";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, rating);
            preparedStatement.setString(3, absolutePath);
            preparedStatement.setString(4, lastViewed);
            preparedStatement.setString(5, imdbRating);
            preparedStatement.execute();
        }
    }

    /**
     * updates movie with given id in db
     * @param id
     * @param name
     * @param rating
     * @param absolutePath
     * @param lastViewed
     * @throws SQLException
     */
    public void updateMovie(int id, String name, String rating, String absolutePath, String lastViewed, String imdbRating) throws SQLException {
        try(Connection connection = dbConnection.getConnection()){
            String sql = "UPDATE Movie SET movie_title = ?, user_rating = ? absolute_path = ? last_viewed = ? imdb_rating = ? WHERE id = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, rating);
            preparedStatement.setString(3, absolutePath);
            preparedStatement.setString(4, lastViewed);
            preparedStatement.setInt(5, id);
            preparedStatement.setString(6, imdbRating);
            preparedStatement.execute();
        }
    }
    public void updateTitle(Movie movie) throws SQLException {
        String title = movie.getName();
        int id = movie.getId();

        try(Connection connection = dbConnection.getConnection()){
            String sql = "UPDATE Movie SET movie_title = ? WHERE id = ? ;";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, title);
            preparedStatement.setInt(2, id);
            preparedStatement.execute();
        }
    }
    public void updateIMDBRating(Movie movie) throws SQLException {
        String rating = movie.getImdbRating();
        String title = movie.getName();

        try(Connection connection = dbConnection.getConnection()){
            String sql = "UPDATE Movie SET imdb_rating = ? WHERE movie_title = ? ;";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, rating);
            preparedStatement.setString(2, title);
            preparedStatement.execute();
        }
    }
    public void updateUserRating(Movie movie) throws SQLException {
        String rating = movie.getRating();
        String title = movie.getName();

        try(Connection connection = dbConnection.getConnection()){
            String sql = "UPDATE Movie SET user_rating = ? WHERE movie_title = ? ;";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, rating);
            preparedStatement.setString(2, title);
            preparedStatement.execute();
        }
    }


    public HashMap<Category, Movie> getAllCatMovies() throws SQLException {
        HashMap<Category, Movie> allCatMovies = new HashMap<>();
        Category category;
        Movie movie;
        try(Connection connection = dbConnection.getConnection()){
            String sql = "SELECT *\n" +
                    "FROM Movie m \n" +
                    "INNER JOIN CatMovie cm \n" +
                    "    ON m.id = cm.movie_id\n" +
                    "RIGHT JOIN Category c\n" +
                    "    ON cm.category_id = c.id";
            preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                int movieId = rs.getInt("movie_id");
                String movieTitle = rs.getString("movie_title");
                String rating = rs.getString("user_rating");
                String absolutePath = rs.getString("absolute_path");
                String lastViewed = rs.getString("last_viewed");
                String imdbRating = rs.getString("imdb_rating");
                int categoryID = rs.getInt("category_id");
                String categoryName = rs.getString("category_name");
                category = new Category(categoryID, categoryName);
                movie = new Movie(movieId, movieTitle, rating, absolutePath, lastViewed, imdbRating);
                allCatMovies.put(category, movie);
            }
            return allCatMovies;
        }
    }

}
