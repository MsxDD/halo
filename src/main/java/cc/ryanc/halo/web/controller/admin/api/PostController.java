package cc.ryanc.halo.web.controller.admin.api;

import cc.ryanc.halo.model.dto.post.PostDetailOutputDTO;
import cc.ryanc.halo.model.dto.post.PostMinimalOutputDTO;
import cc.ryanc.halo.model.dto.post.PostSimpleOutputDTO;
import cc.ryanc.halo.model.entity.Post;
import cc.ryanc.halo.model.enums.PostStatus;
import cc.ryanc.halo.model.enums.PostType;
import cc.ryanc.halo.model.params.PostParam;
import cc.ryanc.halo.service.PostService;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.data.domain.Sort.Direction.DESC;

/**
 * Post controller.
 *
 * @author johnniang
 * @date 3/19/19
 */
@RestController
@RequestMapping("/admin/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("latest")
    @ApiOperation("Pages latest post")
    public List<PostMinimalOutputDTO> pageLatest(@RequestParam(name = "top", defaultValue = "10") int top) {
        return postService.pageLatestOfMinimal(top).getContent();
    }

    @GetMapping("status/{status}")
    @ApiOperation("Gets a page of post by post status")
    public Page<PostSimpleOutputDTO> pageByStatus(@PathVariable(name = "status") PostStatus status,
                                                  @PageableDefault(sort = "updateTime", direction = DESC) Pageable pageable) {
        return postService.pageSimpleDtoByStatus(status, PostType.POST, pageable);
    }

    @PostMapping
    public PostDetailOutputDTO createBy(@Valid @RequestBody PostParam postParam) {
        // Convert to
        Post post = postParam.convertTo();

        Post createdPost = postService.createBy(post, postParam.getTagIds(), postParam.getCategoryIds());

        return new PostDetailOutputDTO().convertFrom(createdPost);
    }

    @PutMapping("{postId:\\d+}")
    public PostDetailOutputDTO updateBy(@Valid @RequestBody PostParam postParam,
                                        @PathVariable("postId") Integer postId) {
        // Get the post info
        Post postToUpdate = postService.getById(postId);

        postParam.update(postToUpdate);

        Post updatedPost = postService.updateBy(postToUpdate, postParam.getTagIds(), postParam.getCategoryIds());

        return new PostDetailOutputDTO().convertFrom(updatedPost);
    }

}