const API_LIMITER_RULES: ApiLimiterEntry = {
    "/endpoint_name_1": {
        "POST": {
            time: 18,
            limit: 50
        },
        "GET": {
            time: 14,
            limit: 60
        }
        //...
    },
    "/endpoint_name_2": {
        "POST": {
            time: 15,
            limit: 30,
        }
        //...
    },

    "/endpoint_name_3": {
        "PUT": {
            time: 20,
            limit: 100
        },
        "DELETE":  {
            time: 20,
            limit: 100
        },
        //...
    }
}
