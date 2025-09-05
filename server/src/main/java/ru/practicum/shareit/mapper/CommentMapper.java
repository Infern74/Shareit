package ru.practicum.shareit.mapper;

import ru.practicum.shareit.dto.item.CommentDto;
import ru.practicum.shareit.model.Comment;
import ru.practicum.shareit.model.Item;
import ru.practicum.shareit.model.User;

import java.time.LocalDateTime;

public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    public static Comment toComment(CommentDto commentDto, Item item, User author, LocalDateTime created) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(created);
        return comment;
    }
}
