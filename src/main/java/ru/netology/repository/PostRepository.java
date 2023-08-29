package ru.netology.repository;

import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

// Stub-корешок, квитанция,пень
public class PostRepository {

    private static Map<Long, Post> posts = new ConcurrentHashMap<>();
    private static AtomicLong count;

    public List<Post> all() {
        return new ArrayList<>(posts.values());
    }

    public Optional<Post> getById(long id) {
        if (posts.containsKey(id)) {
            return Optional.ofNullable(posts.get(id));
        }
        System.out.println("Такого поста не существует");
        return Optional.empty();
    }

    public Post save(Post post) {
        if (post.getId() == 0 || !posts.containsKey(post.getId())) {
            Post newPost = new Post(count.get(), post.getContent());
            posts.put(count.get(), newPost);
            count.getAndIncrement();
            return newPost;
        } else if (post.getId() != 0 && posts.containsKey(post.getId())) {
            posts.replace(post.getId(), post);
            return post;
        }
        return null;
    }

    public void removeById(long id) {
        if (posts.containsKey(id)) {
            posts.remove(id);
        } else {
            throw new NotFoundException();
        }
    }
}
