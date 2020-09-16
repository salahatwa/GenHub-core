package com.genhub.domain.dto.blog;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.genhub.domain.blog.Comment;
import com.genhub.service.blog.CommentService;
import com.genhub.service.blog.PostService;
import com.genhub.service.blog.UserService;
import com.genhub.utils.BeanMapUtils;
import com.genhub.utils.SpringUtils;


public class CommentComplementor {
    private List<CommentVO> comments = Lists.newArrayList();
    private Set<Long> userIds = Sets.newHashSet();
    private Set<Long> postIds = Sets.newHashSet();
    private Set<Long> parentIds = Sets.newHashSet();

    public static CommentComplementor of(List<Comment> entities) {
        CommentComplementor builder = new CommentComplementor();

        entities.forEach(po -> {
            if (po.getPid() > 0) {
                builder.parentIds.add(po.getPid());
            }
            builder.userIds.add(po.getAuthorId());
            builder.postIds.add(po.getPostId());
            builder.comments.add(BeanMapUtils.copy(po));
        });

        return builder;
    }

    public CommentComplementor flutBuildUser() {
        Map<Long, UserVO> map = SpringUtils.getBean(UserService.class).findMapByIds(this.userIds);
        comments.forEach(p -> p.setAuthor(map.get(p.getAuthorId())));
        return this;
    }

    public CommentComplementor flutBuildPost() {
        Map<Long, PostVO> map = SpringUtils.getBean(PostService.class).findMapByIds(this.postIds);
        comments.forEach(p -> p.setPost(map.get(p.getAuthorId())));
        return this;
    }

    public CommentComplementor flutBuildParent() {
        if (!parentIds.isEmpty()) {
            Map<Long, CommentVO> pm = SpringUtils.getBean(CommentService.class).findByIds(parentIds);

            comments.forEach(c -> {
                if (c.getPid() > 0) {
                    c.setParent(pm.get(c.getPid()));
                }
            });
        }
        return this;
    }

    public List<CommentVO> getComments() {
        return comments;
    }

    public Map<Long, CommentVO> toMap() {
        return comments.stream().collect(Collectors.toMap(CommentVO::getId, n-> n));
    }

}
