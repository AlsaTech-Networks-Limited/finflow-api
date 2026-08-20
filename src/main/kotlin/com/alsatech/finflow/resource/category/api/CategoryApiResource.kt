package com.alsatech.finflow.resource.category.api

import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.RequestPredicates.*
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerResponse

@Service
class CategoryApiResource {

    @Bean(name = ["categoryApiRoute"])
    fun routes(categoryApiHandler: CategoryApiHandler): RouterFunction<ServerResponse> {
        return RouterFunctions
            .route(POST(BASE_ROUTE).and(accept(MediaType.APPLICATION_JSON)), categoryApiHandler::createCategory)
            .andRoute(GET(CATEGORY_BY_ID).and(accept(MediaType.APPLICATION_JSON)), categoryApiHandler::fetchCategoryById)
            .andRoute(GET(BASE_ROUTE).and(accept(MediaType.APPLICATION_JSON)), categoryApiHandler::fetchAllCategories)
            .andRoute(GET(CATEGORY_TREE).and(accept(MediaType.APPLICATION_JSON)), categoryApiHandler::fetchCategoryTree)
    }
}
