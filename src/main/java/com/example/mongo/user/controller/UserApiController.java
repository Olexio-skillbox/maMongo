package com.example.mongo.user.controller;

import com.example.mongo.user.dto.request.CreateUserRequest;
import com.example.mongo.user.dto.request.EditUserRequest;
import com.example.mongo.user.dto.response.UserResponse;
import com.example.mongo.user.entity.UserDoc;
import com.example.mongo.user.exeption.ObjectIdParseException;
import com.example.mongo.user.exeption.UserNotFoundExeption;
import com.example.mongo.user.repository.UserRepository;
import com.example.mongo.user.routes.UserRoutes;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
public class UserApiController {
    private final UserRepository userRepository;

    @GetMapping("/")
    public UserDoc test() {
        List<UserDoc> findTest = userRepository.findByFirstName("fTest1");
        List<UserDoc> findTestList = userRepository.findByLastName("lTest1");


        UserDoc test1 = UserDoc.builder()
                .id(new ObjectId())
                .firstName("fTest1")
                .lastName("lTest1")
                .build();
        userRepository.save(test1);
        return test1;
    }

    @PostMapping(UserRoutes.CREATE)
    public UserResponse create(@RequestBody CreateUserRequest request) {
        UserDoc userDoc = UserDoc.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();
        userDoc = userRepository.save(userDoc);
        return UserResponse.of(userDoc);
    }
    @GetMapping(UserRoutes.BY_ID)
    public UserResponse findById(@PathVariable String id) throws UserNotFoundExeption, ObjectIdParseException {
        if (!ObjectId.isValid(id)) throw new ObjectIdParseException();
        // Before NotFound Exeption //UserDoc userDoc = userRepository.findById(new ObjectId(id)).get();
        UserDoc userDoc = userRepository.findById(new ObjectId(id)).orElseThrow(UserNotFoundExeption::new);
        return UserResponse.of(userDoc);
    }

    @GetMapping(UserRoutes.SEARCH)
    public List<UserResponse> search(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "") String query               // db-09: Filter
    ) {
        Pageable pageable = PageRequest.of(page, size);

        ExampleMatcher exampleMatcher = ExampleMatcher.matchingAny()    // db-09: Filter
                .withMatcher("firstName", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("lastName", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

        Example<UserDoc> example = Example.of(                          // db-09: Filter
                UserDoc.builder().firstName(query).lastName(query).build(), exampleMatcher
        );

        // Page<UserDoc> users = userRepository.findAll(pageable);         // db-08: Return All
        Page<UserDoc> users = userRepository.findAll(example, pageable);   // db-09: Filter

        return users.getContent().stream().map(UserResponse::of).collect(Collectors.toList());
    }

    // bd-10: PUT (edit)
    @PutMapping(UserRoutes.BY_ID)
    public UserResponse edit(@PathVariable String id, @RequestBody EditUserRequest request) throws ObjectIdParseException, UserNotFoundExeption {
        if (!ObjectId.isValid(id)) throw new ObjectIdParseException();

        UserDoc userDoc = userRepository
                .findById(new ObjectId(id))
                .orElseThrow(UserNotFoundExeption::new);

        userDoc.setFirstName(request.getFirstName());
        userDoc.setLastName(request.getLastName());

        userDoc = userRepository.save(userDoc);
        return UserResponse.of(userDoc);
    }

    // bd-11: DELETE
    @DeleteMapping(UserRoutes.BY_ID)
    public String delete(@PathVariable String id) throws ObjectIdParseException {
        if (!ObjectId.isValid(id)) throw new ObjectIdParseException();

        userRepository.deleteById(new ObjectId(id));
        return HttpStatus.OK.name();
    }
}
