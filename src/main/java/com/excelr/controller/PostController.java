package com.excelr.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

import com.excelr.entity.Comment;
import com.excelr.entity.Post;
import com.excelr.entity.SavedPost;
import com.excelr.entity.User;
import com.excelr.repository.CommentRepository;
import com.excelr.service.CommentService;
import com.excelr.service.PostService;
import com.excelr.service.SavedPostService;

import jakarta.servlet.http.HttpSession;

@Controller
public class PostController {
	
	@Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;
    
    @Autowired
    private HttpSession session;
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private SavedPostService savedPostService;

    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        List<Post> posts = postService.getAllPosts();
        model.addAttribute("posts", posts);
        
        User user = (User) session.getAttribute("user");
        if (user != null) {
            // Get all saved post IDs for this user
            List<SavedPost> savedPosts = savedPostService.getSavedPostsByUserId(user.getId());
            List<Integer> savedPostIds = savedPosts.stream()
                    .map(sp -> sp.getPost().getId())
                    .collect(Collectors.toList());
            model.addAttribute("savedPostIds", savedPostIds);
            model.addAttribute("user", user);
        }
        
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("posts", postService.getPostsByUserId(user.getId()));
            return "dashboard";
        }
        return "redirect:/login";
    }

    @GetMapping("/create")
    public String createPostPage(Model model) {
        model.addAttribute("post", new Post());
        return "create_post";
    }

    @PostMapping("/create")
    public String createPost(@Validated @ModelAttribute Post post, HttpSession session, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Please correct the errors below.");
            return "create_post";
        }

        //  FIX: Set the logged-in user to the post
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        post.setUser(user);

        postService.createPost(post);
        
        

        return "redirect:/dashboard";
    }



    @GetMapping("/view/{id}")
    public String viewPost(@PathVariable Integer id, Model model, HttpSession session) {
        Post post = postService.getPostById(id);

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        List<Comment> comments = commentService.getCommentsByPostId(id);
        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        model.addAttribute("user", user);
        model.addAttribute("commentObj", new Comment());
        
        // Check if post is saved
        boolean isSaved = savedPostService.isPostSaved(user, post);
        model.addAttribute("isSaved", isSaved);

        return "view_post";
    }


    @PostMapping("/comment/{postId}")
    public String addComment(@PathVariable Integer postId, @ModelAttribute Comment comment) {
    	User currentUser = (User) session.getAttribute("user");

        // Get the post by ID
        Post post = postService.getPostById(postId);

        // Check if the current user is the author of the post
        if (currentUser != null && currentUser.getId().equals(post.getUser().getId())) {
            // If the user is the author of the post, don't allow commenting
            return "redirect:/view/" + postId + "?error=cannot_comment_on_own_post";
        }

        // Otherwise, proceed with commenting
        comment.setPost(post);
        commentService.addComment(comment);
        return "redirect:/view/" + postId;
    }
    
    @GetMapping("/search")
    public String searchPosts(@RequestParam("query") String query, Model model, HttpSession session) {
        List<Post> posts = postService.searchPosts(query);
        model.addAttribute("posts", posts);
        model.addAttribute("query", query);
        
        User user = (User) session.getAttribute("user");
        if (user != null) {
            List<SavedPost> savedPosts = savedPostService.getSavedPostsByUserId(user.getId());
            List<Integer> savedPostIds = savedPosts.stream()
                    .map(sp -> sp.getPost().getId())
                    .collect(Collectors.toList());
            model.addAttribute("savedPostIds", savedPostIds);
            model.addAttribute("user", user);
        }
        
        return "index";
    }
    
    @GetMapping("/edit/{id}")
    public String editPost(@PathVariable Integer id, Model model) {
        Post post = postService.getPostById(id);  // Retrieve the post by ID

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";  // Ensure the user is logged in
        }

        // Check if the logged-in user is the author of the post
        if (post == null || !post.getUser().getId().equals(user.getId())) {
            return "redirect:/dashboard";  // Redirect to the dashboard if the post doesn't exist or if it's not the logged-in user's post
        }

        model.addAttribute("post", post);  // Add the post to the model for the edit form
        return "edit_post";  // Return the "edit_post" view
    }

    @PostMapping("/edit/{id}")
    public String updatePost(@PathVariable Integer id, @ModelAttribute Post post, HttpSession session) {
        Post existingPost = postService.getPostById(id);

        User user = (User) session.getAttribute("user");
        if (user == null || existingPost == null || !existingPost.getUser().getId().equals(user.getId())) {
            return "redirect:/dashboard";  // Ensure the user is logged in and authorized to edit the post
        }

        // Update the post with the new data
        existingPost.setTitle(post.getTitle());
        existingPost.setDescription(post.getDescription());
        existingPost.setContent(post.getContent());

        postService.createPost(existingPost);  // Save the updated post

        return "redirect:/dashboard";  // Redirect to the dashboard after updating
        
    }
    
    @GetMapping("/comments")
    public String viewUserComments(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("user");

        if (loggedInUser == null) {
            return "redirect:/login";
        }

        List<Comment> comments = commentRepository.findCommentsByPostId(loggedInUser.getId());
        model.addAttribute("comments", comments);

        return "comments"; // Thymeleaf template name
    }
    
    

    @GetMapping("/posts/new")
    public String showNewPostForm(Model model) {
        model.addAttribute("post", new Post());
        return "create_post"; // or use new_post.html if you're using a different view
    }
    @GetMapping("/posts")
    public String showAllPosts(HttpSession session, Model model) {
        List<Post> posts = postService.getAllPosts();
        User currentUser = (User) session.getAttribute("user");

        model.addAttribute("posts", posts);
        model.addAttribute("user", currentUser);
        
        if (currentUser != null) {
            List<SavedPost> savedPosts = savedPostService.getSavedPostsByUserId(currentUser.getId());
            List<Integer> savedPostIds = savedPosts.stream()
                    .map(sp -> sp.getPost().getId())
                    .collect(Collectors.toList());
            model.addAttribute("savedPostIds", savedPostIds);
        }
        
        return "index";
    }




    @GetMapping("/delete/{id}")
    public String deletePost(@PathVariable Integer id) {
        postService.deletePost(id);
        return "redirect:/dashboard";
    }
    
    // Save/Unsave a post
    @PostMapping("/save/{postId}")
    public String savePost(@PathVariable Integer postId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        Post post = postService.getPostById(postId);
        if (post == null) {
            return "redirect:/";
        }
        
        savedPostService.savePost(user, post);
        return "redirect:/view/" + postId;
    }

    @PostMapping("/unsave/{postId}")
    public String unsavePost(@PathVariable Integer postId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        Post post = postService.getPostById(postId);
        if (post == null) {
            return "redirect:/";
        }
        
        savedPostService.unsavePost(user, post);
        return "redirect:/view/" + postId;
    }

    // View saved posts page
    @GetMapping("/saved")
    public String viewSavedPosts(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        List<Post> savedPosts = savedPostService.getSavedPostsAsPosts(user.getId());
        model.addAttribute("posts", savedPosts);
        model.addAttribute("user", user);
        
        return "saved_posts";
    }

}
