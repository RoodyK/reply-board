package com.replyboard.api.service.comment;

import com.replyboard.api.controller.comment.request.CreateCommentRequest;
import com.replyboard.api.controller.comment.request.EditCommentRequest;
import com.replyboard.api.controller.comment.request.RemoveCommentRequest;
import com.replyboard.domain.category.Category;
import com.replyboard.domain.category.CategoryRepository;
import com.replyboard.domain.comment.Comment;
import com.replyboard.domain.comment.CommentRepository;
import com.replyboard.domain.member.Member;
import com.replyboard.domain.member.MemberRepository;
import com.replyboard.domain.member.Role;
import com.replyboard.domain.post.Post;
import com.replyboard.domain.post.PostRepository;
import com.replyboard.domain.post.PostStatus;
import com.replyboard.exception.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @DisplayName("댓글을 등록한다")
    @Test
    void addComment() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Category category = createCategory("기타", member);
        categoryRepository.save(category);

        Post post = createPost(member, category);
        postRepository.save(post);

        CreateCommentRequest request = CreateCommentRequest.builder()
                .author("노을")
                .password("1234")
                .content("좋은 글입니다.")
                .build();

        // when
        Long savedId = commentService.addComment(post.getId(), request.toServiceRequest());

        // then
        assertThat(savedId).isNotNull();
        Comment findComment = commentRepository.findById(savedId).orElseThrow(CommentNotFoundException::new);
        assertThat(findComment).isNotNull();
        assertThat(findComment.getAuthor()).isEqualTo("노을");
        assertThat(passwordEncoder.matches(request.getPassword(), findComment.getPassword())).isTrue();
        assertThat(findComment.getContent()).isEqualTo("좋은 글입니다.");
        assertThat(findComment.getPost()).isEqualTo(post);
    }

    @DisplayName("댓글을 등록할 때 게시글이 존재하지 않으면 예외가 발생한다.")
    @Test
    void addCommentNotExistsPost() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Category category = createCategory("기타", member);
        categoryRepository.save(category);

        Post post = createPost(member, category);
        postRepository.save(post);

        CreateCommentRequest request = CreateCommentRequest.builder()
                .author("노을")
                .password("1234")
                .content("좋은 글입니다.")
                .build();

        // when
        assertThatThrownBy(() -> commentService.addComment(post.getId() + 1, request.toServiceRequest()))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessage("게시글을 찾을 수 없습니다.");
    }

    @DisplayName("댓글에 대댓글(답글)을 등록한다")
    @Test
    void addReply() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Category category = createCategory("기타", member);
        categoryRepository.save(category);

        Post post = createPost(member, category);
        postRepository.save(post);

        Comment comment = createComment(post, "노을", "1234", "좋은 글입니다.");
        commentRepository.save(comment);

        CreateCommentRequest request = CreateCommentRequest.builder()
                .author("불멍")
                .password("1234")
                .content("불멍중입니다.")
                .build();

        // when
        Long savedId = commentService.addReply(post.getId(), comment.getId(), request.toServiceRequest());

        // then
        assertThat(savedId).isNotNull();
        Comment reply = commentRepository.findById(savedId).orElseThrow(CommentNotFoundException::new);
        assertThat(reply.getAuthor()).isEqualTo("불멍");
        assertThat(passwordEncoder.matches(request.getPassword(), reply.getPassword())).isTrue();
        assertThat(reply.getContent()).isEqualTo("불멍중입니다.");
        assertThat(reply.getParent()).isEqualTo(comment);
        assertThat(reply.getPost()).isEqualTo(post);
    }

    @DisplayName("댓글에 대댓글(답글)을 등록할 때 게시글을 찾을 수 없으면 예외가 발생한다.")
    @Test
    void addReplyNotExistsPost() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Category category = createCategory("기타", member);
        categoryRepository.save(category);

        Post post = createPost(member, category);
        postRepository.save(post);

        Comment comment = createComment(post, "노을", "1234", "좋은 글입니다.");
        commentRepository.save(comment);

        CreateCommentRequest request = CreateCommentRequest.builder()
                .author("불멍")
                .password("1234")
                .content("불멍중입니다.")
                .build();

        // when
        assertThatThrownBy(() -> commentService.addReply(post.getId() + 1, comment.getId(), request.toServiceRequest()))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessage("게시글을 찾을 수 없습니다.");
    }

    @DisplayName("댓글에 대댓글(답글)을 등록할 때 부모 댓글을 찾을 수 없으면 예외가 발생한다.")
    @Test
    void addReplyNotExistsComment() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Category category = createCategory("기타", member);
        categoryRepository.save(category);

        Post post = createPost(member, category);
        postRepository.save(post);

        Comment comment = createComment(post, "노을", "1234", "좋은 글입니다.");
        commentRepository.save(comment);

        CreateCommentRequest request = CreateCommentRequest.builder()
                .author("불멍")
                .password("1234")
                .content("불멍중입니다.")
                .build();

        // when
        assertThatThrownBy(() -> commentService.addReply(post.getId(), comment.getId() + 1, request.toServiceRequest()))
                .isInstanceOf(CommentNotFoundException.class)
                .hasMessage("댓글을 찾을 수 없습니다.");
    }

    @DisplayName("대댓글에 또다른 댓글을 달 수 없다.")
    @Test
    void addReplyAnotherReply() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Category category = createCategory("기타", member);
        categoryRepository.save(category);

        Post post = createPost(member, category);
        postRepository.save(post);

        Comment comment = createComment(post, "노을", "1234", "좋은 글입니다.");
        commentRepository.save(comment);

        Comment reply = Comment.builder()
                .author("선수치지")
                .password("1234")
                .content("댓글 먼저 선수치기")
                .build();
        reply.addParentComment(comment);
        reply.addPost(post);
        commentRepository.save(reply);

        CreateCommentRequest request = CreateCommentRequest.builder()
                .author("불멍")
                .password("1234")
                .content("불멍중입니다.")
                .build();

        // when
        assertThatThrownBy(() -> commentService.addReply(post.getId(), reply.getId(), request.toServiceRequest()))
                .isInstanceOf(CanNotAnotherReplyException.class)
                .hasMessage("답글에 댓글을 달 수 없습니다.");
    }

    @DisplayName("댓글을 삭제한다")
    @Test
    void removeComment() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Category category = createCategory("기타", member);
        categoryRepository.save(category);

        Post post = createPost(member, category);
        postRepository.save(post);

        Comment comment = createComment(post, "노을", "1234", "좋은 글입니다.");
        commentRepository.save(comment);

        RemoveCommentRequest request = RemoveCommentRequest.builder()
                .password("1234")
                .build();

        // when
        commentService.removeComment(post.getId(), comment.getId(), request.toServiceRequest());

        // then
        List<Comment> findComment = commentRepository.findAll();
        assertThat(findComment).isEmpty();
    }

    @DisplayName("댓글을 삭제할 때 비밀번호가 다르면 예외가 발생한다.")
    @Test
    void removeCommentNotMatchPassword() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Category category = createCategory("기타", member);
        categoryRepository.save(category);

        Post post = createPost(member, category);
        postRepository.save(post);

        Comment comment = createComment(post, "노을", "1234", "좋은 글입니다.");
        commentRepository.save(comment);

        RemoveCommentRequest request = RemoveCommentRequest.builder()
                .password("12345")
                .build();

        // when
        assertThatThrownBy(() -> commentService.removeComment(post.getId(), comment.getId(), request.toServiceRequest()))
                .isInstanceOf(NotMatchPasswordException.class)
                .hasMessage("비밀번호가 일치하지 않습니다.");
    }

    @DisplayName("댓글을 삭제할 때 게시글이 다르면 예외가 발생한다.")
    @Test
    void removeCommentNotMatchPost() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Category category = createCategory("기타", member);
        categoryRepository.save(category);

        Post post = createPost(member, category);
        Post post2 = createPost(member, category);
        postRepository.save(post);
        postRepository.save(post2);

        Comment comment = createComment(post, "노을", "1234", "좋은 글입니다.");
        commentRepository.save(comment);

        RemoveCommentRequest request = RemoveCommentRequest.builder()
                .password("1234")
                .build();

        // when
        assertThatThrownBy(() -> commentService.removeComment(post2.getId(), comment.getId(), request.toServiceRequest()))
                .isInstanceOf(NotSamePostException.class)
                .hasMessage("댓글이 작성된 게시글과 일치하지 않습니다.");
    }

    @DisplayName("댓글을 수정한다.")
    @Test
    void editComment() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Category category = createCategory("기타", member);
        categoryRepository.save(category);

        Post post = createPost(member, category);
        postRepository.save(post);

        Comment comment = createComment(post, "노을", "1234", "좋은 글입니다.");
        commentRepository.save(comment);

        EditCommentRequest request = EditCommentRequest.builder()
                .password("1234")
                .content("글을 수정합니다.")
                .build();

        // when
        commentService.editComment(post.getId(), comment.getId(), request.toServiceRequest());

        // then
        Comment findComment = commentRepository.findAll().get(0);
        assertThat(findComment.getContent()).isEqualTo("글을 수정합니다.");
    }

    @DisplayName("댓글을 수정할 때 댓글을 등록한 게시글과 일치하지 않으면 예외가 발생한다..")
    @Test
    void editCommentNotSamePost() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Category category = createCategory("기타", member);
        categoryRepository.save(category);

        Post post = createPost(member, category);
        Post post2 = createPost(member, category);
        postRepository.save(post);
        postRepository.save(post2);

        Comment comment = createComment(post, "노을", "1234", "좋은 글입니다.");
        commentRepository.save(comment);

        EditCommentRequest request = EditCommentRequest.builder()
                .password("1234")
                .content("글을 수정합니다.")
                .build();

        // when
        assertThatThrownBy(() -> commentService.editComment(post2.getId(), comment.getId(), request.toServiceRequest()))
                .isInstanceOf(NotSamePostException.class)
                .hasMessage("댓글이 작성된 게시글과 일치하지 않습니다.");
    }

    @DisplayName("댓글을 수정할 때 댓글 비밀번호와 일치하지 않으면 예외가 발생한다..")
    @Test
    void editCommentNotMatchPassword() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Category category = createCategory("기타", member);
        categoryRepository.save(category);

        Post post = createPost(member, category);
        postRepository.save(post);

        Comment comment = createComment(post, "노을", "1234", "좋은 글입니다.");
        commentRepository.save(comment);

        EditCommentRequest request = EditCommentRequest.builder()
                .password("12345")
                .content("글을 수정합니다.")
                .build();

        // when
        assertThatThrownBy(() -> commentService.editComment(post.getId(), comment.getId(), request.toServiceRequest()))
                .isInstanceOf(NotMatchPasswordException.class)
                .hasMessage("비밀번호가 일치하지 않습니다.");
    }

    private Comment createComment(Post post, String author, String password, String content) {
        Comment comment = Comment.builder()
                .author(author)
                .password(passwordEncoder.encode(password))
                .content(content)
                .build();
        comment.addPost(post);
        return comment;
    }

    private Post createPost(Member member, Category category) {
        Post post = Post.builder()
                .title("글 등록하기")
                .content("첫 게시글 입니다.")
                .postStatus(PostStatus.PRIVATE)
                .build();
        post.addMember(member);
        post.addCategory(category);
        return post;
    }

    private Member createMember() {
        Member member = Member.builder()
                .name("루디")
                .email("test@test.com")
                .password(passwordEncoder.encode("1234"))
                .build();

        member.addRole(Set.of(Role.ROLE_ADMIN));

        return member;
    }

    private Category createCategory(String name, Member member) {
        Category category = Category.builder()
                .name(name)
                .build();
        category.addMember(member);

        return category;
    }

}