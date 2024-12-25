package com.example.notes.config;

import com.example.notes.resource.Note;
import com.example.notes.resource.Tenant;
import com.example.notes.resource.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
public class RestRepositoryConfig implements RepositoryRestConfigurer {

    @Autowired
    private Validator validator;

    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
        cors.addMapping("*");
        config.setBasePath("/api");
        config.setReturnBodyForPutAndPost(true);
        config.setReturnBodyOnCreate(true);
        config.setReturnBodyOnUpdate(true);
        config.exposeIdsFor(User.class);
        config.exposeIdsFor(Tenant.class);
        config.exposeIdsFor(Note.class);
    }

    public void configureValidatingRepositoryEventListener(ValidatingRepositoryEventListener validatingListener) {
        validatingListener.addValidator("beforeCreate", validator);
        validatingListener.addValidator("beforeUpdate", validator);
    }

}
