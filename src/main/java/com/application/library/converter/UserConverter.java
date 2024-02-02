package com.application.library.converter;

import com.application.library.data.dto.user.BaseUserDto;
import com.application.library.data.dto.user.BaseUserSaveRequestDto;
import com.application.library.data.dto.user.UserSaveRequestDto;
import com.application.library.model.User;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
        collectionMappingStrategy = CollectionMappingStrategy.SETTER_PREFERRED,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {}
)
public abstract class UserConverter {


    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mapping(source = "dto.password", target = "password", qualifiedByName = "encodePassword")
    public abstract User toEntity(BaseUserSaveRequestDto dto);

    @Mapping(source = "dto.password", target = "password", qualifiedByName = "encodePassword")
    @Mapping(source = "dto.roles", target = "authorities")
    public abstract User toEntity(UserSaveRequestDto dto);

    @Mapping(source = "dto.password", target = "password", qualifiedByName = "encodePassword")
    @Mapping(source = "dto.roles", target = "authorities")
    public abstract User updateEntity(UserSaveRequestDto dto, @MappingTarget User user);


    @Mapping(source = "dto.password", target = "password", qualifiedByName = "encodePassword")
    public abstract User updateEntity(BaseUserSaveRequestDto dto, @MappingTarget User user);

    public abstract BaseUserDto toBaseDto(User user);


    @Named("encodePassword")
    String encodePassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

}
