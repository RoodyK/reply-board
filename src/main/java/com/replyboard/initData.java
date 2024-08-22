package com.replyboard;

import com.replyboard.api.controller.post.request.CreatePostRequest;
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
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

@Profile("local")
@Component
@RequiredArgsConstructor
public class initData {

    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.initMember();
    }

    @Profile("local")
    @Transactional
    @Component
    @RequiredArgsConstructor
    static class InitService {

        private final MemberRepository memberRepository;
        private final PasswordEncoder passwordEncoder;
        private final CategoryRepository categoryRepository;
        private final PostRepository postRepository;
        private final CommentRepository commentRepository;

        public void initMember() {
            Member member1 = Member.builder()
                    .name("루디")
                    .email("admin@test.com")
                    .password(passwordEncoder.encode("1234"))
                    .build();

            member1.addRole(Set.of(Role.ROLE_ADMIN));

            Member member2 = Member.builder()
                    .name("유저")
                    .email("user@test.com")
                    .password(passwordEncoder.encode("1234"))
                    .build();

            member2.addRole(Set.of(Role.ROLE_USER));

            memberRepository.saveAll(List.of(member1, member2));

            Category category1 = createCategory("운동", member1);
            Category category2 = createCategory("요리", member1);
            Category category3 = createCategory("게임", member1);
            categoryRepository.saveAll(List.of(category1, category2, category3));

            IntStream.range(1, 102)
                    .mapToObj(i -> {
                        if (i % 3 == 1) {
                            CreatePostRequest postRequest = createPostRequest("오늘 날씨는", "무더위와 열대야가 이어지는 가운데 전국 대부분 가끔 비가 오는 곳이 있겠다. 제주도와 경남권 해안에는 가끔 비가 내리겠으며 오전부터 강원 산지와 그 밖의 남부 지방에, 오후부터는 중부 지방에 가끔 비가 오는 곳이 있겠다.");
                            return Post.createPost(postRequest.toServiceRequest().toPostDto(), member1, category1);
                        }
                        if (i % 3 == 2) {
                            CreatePostRequest postRequest = createPostRequest("낙화", "가야 할 때가 언제인가를\n" +
                                    "분명히 알고 가는 이의\n" +
                                    "뒷모습은 얼마나 아름다운가.\n" +
                                    "\n" +
                                    "봄 한철\n" +
                                    "격정을 인내한\n" +
                                    "나의 사랑은 지고 있다.\n" +
                                    "\n" +
                                    "분분한 낙화……\n" +
                                    "결별이 이룩하는 축복에 싸여\n" +
                                    "지금은 가야 할 때,\n" +
                                    "\n" +
                                    "무성한 녹음과 그리고\n" +
                                    "머지않아 열매 맺는\n" +
                                    "가을을 향하여\n" +
                                    "\n" +
                                    "나의 청춘은 꽃답게 죽는다.\n" +
                                    "헤어지자\n" +
                                    "섬세한 손길을 흔들며\n" +
                                    "하롱하롱 꽃잎이 지는 어느 날\n" +
                                    "\n" +
                                    "나의 사랑, 나의 결별,\n" +
                                    "샘터에 물 고이듯 성숙하는\n" +
                                    "내 영혼의 슬픈 눈.");
                            return Post.createPost(postRequest.toServiceRequest().toPostDto(), member1, category2);
                        }
                        CreatePostRequest postRequest = createPostRequest("호흡", "일반적으로 호흡은 숨을 쉬는 것으로 이해하고 있다. 숨을 들이쉴 때마다 산소를 섭취하고, 내쉴 때마다 이산화탄소를 배출한다. 이처럼 외부 환경으로부터 산소를 섭취하고 이산화탄소를 배출하는 물리적 과정은 생리학적 또는 거시적인 의미의 호흡이다.\n" +
                                "\n" +
                                "생화학적 또는 미시적인 의미의 호흡은 생물의 세포가 영양물질을 물과 이산화탄소로 산화시켜 에너지를 얻는 화학적 과정이다.\n" +
                                "[네이버 지식백과] 호흡 [respiration] (생화학백과)");
                        return Post.createPost(postRequest.toServiceRequest().toPostDto(), member1, category3);
                    })
                    .forEach(postRepository::save);

            Category category = createCategory("기타", member1);
            categoryRepository.save(category);

            Post post = createPost(member1, category);
            postRepository.save(post);

            Comment comment1 = createComment(post, "노을", "1234", "좋은 글입니다.");
            Comment comment2 = createComment(post, "김밥", "12345", "유용한 정보입니다.");
            commentRepository.saveAll(List.of(comment1, comment2));

            Comment reply1 = createComment(post, "리플러", "1234", "답변 남깁니다.");
            comment1.addReply(reply1);

            Comment reply2 = createComment(post, "방랑가", "1234", "포스팅 감사합니다.");
            comment1.addReply(reply2);

            Comment reply3 = createComment(post, "파도", "1234", "좋은 정보 감사합니다.");
            comment2.addReply(reply3);

            Comment reply4 = createComment(post, "새벽", "1234", "유용한 내용입니다.");
            comment2.addReply(reply4);

            commentRepository.saveAll(List.of(reply1, reply2, reply3, reply4));
        }

        private Post createPost(Member member, Category category) {
            Post post = Post.builder()
                    .title("글 등록하기")
                    .content("첫 게시글 입니다.")
                    .postStatus(PostStatus.PUBLIC)
                    .build();
            post.addMember(member);
            post.addCategory(category);
            return post;
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

        private CreatePostRequest createPostRequest(String title, String content) {
            return CreatePostRequest.builder()
                    .title(title)
                    .content(content)
                    .postStatus(PostStatus.PUBLIC)
                    .build();
        }


        private Category createCategory(String name, Member member) {
            Category category = Category.builder()
                    .name(name)
                    .build();
            category.addMember(member);

            return category;
        }
    }
}
