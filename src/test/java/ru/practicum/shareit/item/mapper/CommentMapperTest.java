package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class CommentMapperTest {
    private final CommentMapper commentMapper = new CommentMapper();

    @Test
    void toCommentDto() {
        Item item = new Item();
        item.setId(1);
        User user = new User();
        user.setId(8);

        Comment comment = new Comment();
        comment.setId(2);
        comment.setText("text 2");
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());

        CommentDto commentDto = commentMapper.mapToCommentDto(comment);
        assertThat(comment.getId(), equalTo(commentDto.getId()));
        assertThat(comment.getText(), equalTo(commentDto.getText()));
        assertThat(comment.getAuthor().getName(), equalTo(commentDto.getAuthorName()));
        assertThat(comment.getCreated(), equalTo(commentDto.getCreated()));
    }

    @Test
    void toComment() {
        Item item = new Item();
        item.setId(1);
        User user = new User();
        user.setId(8);

        CommentDto commentDto = new CommentDto(2, "text", 1, "", LocalDateTime.now());

        Comment comment1 = commentMapper.mapToComment(commentDto);
        assertThat(2, equalTo(commentDto.getId()));
        assertThat(comment1.getText(), equalTo(commentDto.getText()));
    }
}
