/* eslint-disable */
/* tslint:disable */
// @ts-nocheck
/*
 * ---------------------------------------------------------------
 * ## THIS FILE WAS GENERATED VIA SWAGGER-TYPESCRIPT-API        ##
 * ##                                                           ##
 * ## AUTHOR: acacode                                           ##
 * ## SOURCE: https://github.com/acacode/swagger-typescript-api ##
 * ---------------------------------------------------------------
 */

export enum OperationType {
    ADD = "ADD",
    TRANSFER = "TRANSFER",
    REMOVE = "REMOVE",
}

export interface TeamDTOSchema {
    name?: string;
    /**
     * The size of team of heroes
     * @format int32
     * @exclusiveMin 0
     */
    size?: number;
}

export type TeamFullSchema = {
    /**
     * ID of a team
     * @format int64
     * @exclusiveMin 0
     */
    id?: number;
} & TeamDTOSchema;

export interface TeamPaginatedSchema {
    teams?: TeamFullSchema[];
    /**
     * Page number (nullable if not paginated)
     * @default null
     */
    page?: number;
    /**
     * Page size (nullable if not paginated)
     * @default null
     */
    pageSize?: number;
    /**
     * Total pages (nullable if not paginated)
     * @default null
     */
    totalPages?: number;
    /**
     * Total count (nullable if not paginated)
     * @default null
     */
    totalCount?: number;
}

export interface TeamPatchSchema {
    /**
     * ID of a human being
     * @format int64
     * @exclusiveMin 0
     */
    humanId?: number;
    /** ID of a team */
    teamId?: number | null;
    operation?: OperationType;
}

export type QueryParamsType = Record<string | number, any>;
export type ResponseFormat = keyof Omit<Body, "body" | "bodyUsed">;

export interface FullRequestParams extends Omit<RequestInit, "body"> {
    /** set parameter to `true` for call `securityWorker` for this request */
    secure?: boolean;
    /** request path */
    path: string;
    /** content type of request body */
    type?: ContentType;
    /** query params */
    query?: QueryParamsType;
    /** format of response (i.e. response.json() -> format: "json") */
    format?: ResponseFormat;
    /** request body */
    body?: unknown;
    /** base url */
    baseUrl?: string;
    /** request cancellation token */
    cancelToken?: CancelToken;
}

export type RequestParams = Omit<
    FullRequestParams,
    "body" | "method" | "query" | "path"
>;

export interface ApiConfig<SecurityDataType = unknown> {
    baseUrl?: string;
    baseApiParams?: Omit<RequestParams, "baseUrl" | "cancelToken" | "signal">;
    securityWorker?: (
        securityData: SecurityDataType | null,
    ) => Promise<RequestParams | void> | RequestParams | void;
    customFetch?: typeof fetch;
}

export interface HttpResponse<D extends unknown, E extends unknown = unknown>
    extends Response {
    data: D;
    error: E;
}

type CancelToken = Symbol | string | number;

export enum ContentType {
    Json = "application/json",
    JsonApi = "application/vnd.api+json",
    FormData = "multipart/form-data",
    UrlEncoded = "application/x-www-form-urlencoded",
    Text = "text/plain",
}

export class HttpClient<SecurityDataType = unknown> {
    public baseUrl: string = "https://localhost:15479/api/v1/heroes";
    private securityData: SecurityDataType | null = null;
    private securityWorker?: ApiConfig<SecurityDataType>["securityWorker"];
    private abortControllers = new Map<CancelToken, AbortController>();
    private customFetch = (...fetchParams: Parameters<typeof fetch>) =>
        fetch(...fetchParams);

    private baseApiParams: RequestParams = {
        credentials: "same-origin",
        headers: {},
        redirect: "follow",
        referrerPolicy: "no-referrer",
    };

    constructor(apiConfig: ApiConfig<SecurityDataType> = {}) {
        Object.assign(this, apiConfig);
    }

    public setSecurityData = (data: SecurityDataType | null) => {
        this.securityData = data;
    };

    protected encodeQueryParam(key: string, value: any) {
        const encodedKey = encodeURIComponent(key);
        return `${encodedKey}=${encodeURIComponent(typeof value === "number" ? value : `${value}`)}`;
    }

    protected addQueryParam(query: QueryParamsType, key: string) {
        return this.encodeQueryParam(key, query[key]);
    }

    protected addArrayQueryParam(query: QueryParamsType, key: string) {
        const value = query[key];
        return value.map((v: any) => this.encodeQueryParam(key, v)).join("&");
    }

    protected toQueryString(rawQuery?: QueryParamsType): string {
        const query = rawQuery || {};
        const keys = Object.keys(query).filter(
            (key) => "undefined" !== typeof query[key],
        );
        return keys
            .map((key) =>
                Array.isArray(query[key])
                    ? this.addArrayQueryParam(query, key)
                    : this.addQueryParam(query, key),
            )
            .join("&");
    }

    protected addQueryParams(rawQuery?: QueryParamsType): string {
        const queryString = this.toQueryString(rawQuery);
        return queryString ? `?${queryString}` : "";
    }

    private contentFormatters: Record<ContentType, (input: any) => any> = {
        [ContentType.Json]: (input: any) =>
            input !== null && (typeof input === "object" || typeof input === "string")
                ? JSON.stringify(input)
                : input,
        [ContentType.JsonApi]: (input: any) =>
            input !== null && (typeof input === "object" || typeof input === "string")
                ? JSON.stringify(input)
                : input,
        [ContentType.Text]: (input: any) =>
            input !== null && typeof input !== "string"
                ? JSON.stringify(input)
                : input,
        [ContentType.FormData]: (input: any) => {
            if (input instanceof FormData) {
                return input;
            }

            return Object.keys(input || {}).reduce((formData, key) => {
                const property = input[key];
                formData.append(
                    key,
                    property instanceof Blob
                        ? property
                        : typeof property === "object" && property !== null
                            ? JSON.stringify(property)
                            : `${property}`,
                );
                return formData;
            }, new FormData());
        },
        [ContentType.UrlEncoded]: (input: any) => this.toQueryString(input),
    };

    protected mergeRequestParams(
        params1: RequestParams,
        params2?: RequestParams,
    ): RequestParams {
        return {
            ...this.baseApiParams,
            ...params1,
            ...(params2 || {}),
            headers: {
                ...(this.baseApiParams.headers || {}),
                ...(params1.headers || {}),
                ...((params2 && params2.headers) || {}),
            },
        };
    }

    protected createAbortSignal = (
        cancelToken: CancelToken,
    ): AbortSignal | undefined => {
        if (this.abortControllers.has(cancelToken)) {
            const abortController = this.abortControllers.get(cancelToken);
            if (abortController) {
                return abortController.signal;
            }
            return void 0;
        }

        const abortController = new AbortController();
        this.abortControllers.set(cancelToken, abortController);
        return abortController.signal;
    };

    public abortRequest = (cancelToken: CancelToken) => {
        const abortController = this.abortControllers.get(cancelToken);

        if (abortController) {
            abortController.abort();
            this.abortControllers.delete(cancelToken);
        }
    };

    public request = async <T = any, E = any>({
                                                  body,
                                                  secure,
                                                  path,
                                                  type,
                                                  query,
                                                  format,
                                                  baseUrl,
                                                  cancelToken,
                                                  ...params
                                              }: FullRequestParams): Promise<HttpResponse<T, E>> => {
        const secureParams =
            ((typeof secure === "boolean" ? secure : this.baseApiParams.secure) &&
                this.securityWorker &&
                (await this.securityWorker(this.securityData))) ||
            {};
        const requestParams = this.mergeRequestParams(params, secureParams);
        const queryString = query && this.toQueryString(query);
        const payloadFormatter = this.contentFormatters[type || ContentType.Json];
        const responseFormat = format || requestParams.format;

        return this.customFetch(
            `${baseUrl || this.baseUrl || ""}${path}${queryString ? `?${queryString}` : ""}`,
            {
                ...requestParams,
                headers: {
                    ...(requestParams.headers || {}),
                    ...(type && type !== ContentType.FormData
                        ? { "Content-Type": type }
                        : {}),
                },
                signal:
                    (cancelToken
                        ? this.createAbortSignal(cancelToken)
                        : requestParams.signal) || null,
                body:
                    typeof body === "undefined" || body === null
                        ? null
                        : payloadFormatter(body),
            },
        ).then(async (response) => {
            const r = response as HttpResponse<T, E>;
            r.data = null as unknown as T;
            r.error = null as unknown as E;

            const responseToParse = responseFormat ? response.clone() : response;
            const data = !responseFormat
                ? r
                : await responseToParse[responseFormat]()
                    .then((data) => {
                        if (r.ok) {
                            r.data = data;
                        } else {
                            r.error = data;
                        }
                        return r;
                    })
                    .catch((e) => {
                        r.error = e;
                        return r;
                    });

            if (cancelToken) {
                this.abortControllers.delete(cancelToken);
            }

            if (!response.ok) throw data;
            return data;
        });
    };
}

/**
 * @title Hero service REST endpoints
 * @version 1.0.3
 * @baseUrl https://localhost:15479/api/v1/heroes
 *
 * This is the first part of our assignment in our course 'Service-Oriented Architecture' where we generate Swagger documentation before implementing it in practice.
 */
export class Api<
    SecurityDataType extends unknown,
> extends HttpClient<SecurityDataType> {
    teams = {
        /**
         * @description **Get all teams** in the collection in the form of a list.
         *
         * @tags Teams
         * @name GetTeams
         * @summary Find all teams.
         * @request GET:/teams
         */
        getTeams: (
            query?: {
                /**
                 * Array of fields used for **sorting**. "-" is used for sorting in **descending** order. If queries and deletion request are provided for one of these fields, independent of "-", an error will be returned.
                 * @default "id"
                 * @example ["-name","size"]
                 */
                sort?: "id" | "name" | "size" | "-id" | "-name" | "-size";
                /**
                 * Array of fields used for **filtering**. Each element must contain the name of the filterable field and the filtering method in square parentheses, as well as the value that will be used for filtering. An _invalid_ value will result in an **error response**. Below is a list of available operations and their code values, their descriptions and available datatypes for said operations.
                 *
                 * ![absolute dandadan](https://media1.tenor.com/m/n_rDQ8ulxIYAAAAd/dandadan-dandadan-anime.gif)
                 * | **Operation**         | **Code** | **Description**                                                                                                               | **Datatype**      |
                 * |-----------------------|----------|-------------------------------------------------------------------------------------------------------------------------------|-------------------|
                 * | Equal                 | eq       | Filters objects by their chosen field and returns objects which have a value equal to the provided                            | String, int, long |
                 * | Not equal             | neq      | Filters objects by their chosen field and returns objects which have a value not equal to the provided                        | String, int, long |
                 * | Greater than          | gt       | Filters objects by their chosen field and returns objects which have a value greater than the provided                        | int, long         |
                 * | Lesser than           | lt       | Filters objects by their chosen field and returns objects which have a value lesser than the provided                         | int, long         |
                 * | Greater than or equal | gte      | Filters objects by their chosen field and returns objects which have a value greater than or equal to the provided            | int, long         |
                 * | Lesser than or equal  | lte      | Filters objects by their chosen field and returns objects which have a value lesser than or equal to the provided             | int, long         |
                 * | Substring search      | like     | Filters objects by their chosen field and returns objects which have a value matched using substring search with the provided | String, int, long |
                 * @example ["name[eq]=Sigma","size[neq]=2","size[gt]=5","size[lte]=9"]
                 */
                filter?: string[];
                /**
                 * Specifies the **page number to be paginated**. If used without the "pageSize" parameter, the page size will be **10** by default. If _none_ of the parameters "page" and "pageSize" are used, then **all** human beings will be returned.
                 * @exclusiveMin 0
                 * @default 1
                 */
                page?: number;
                /**
                 * Specifies the **page size**. If used _without the "page" parameter_, the **first** page will be displayed by default. If _none_ of the parameters "page" and "pageSize" are used, then **all** human beings will be returned.
                 * @min 1
                 * @default 10
                 */
                pageSize?: number;
            },
            params: RequestParams = {},
        ) =>
            this.request<TeamPaginatedSchema, void>({
                path: `/teams`,
                method: "GET",
                query: query,
                format: "json",
                ...params,
            }),

        /**
         * @description **Add** a new team with valid fields.
         *
         * @tags Teams
         * @name AddTeam
         * @summary Add a new team.
         * @request POST:/teams
         */
        addTeam: (data: TeamDTOSchema, params: RequestParams = {}) =>
            this.request<TeamFullSchema, void>({
                path: `/teams`,
                method: "POST",
                body: data,
                type: ContentType.Json,
                format: "json",
                ...params,
            }),

        /**
         * @description For a team with _id = {id}_, **assign, transfer or delete** a human being to a team in the request. | **Operation** | **Code** | **Description**                                                                                                                                      | **Datatype** [humanId, teamId] | |---------------|----------|------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------| | Add           | ADD      | Adds a human being (chosen by humanId) to a team (chosen by teamId)                                                                                  | long, long                     | | Transfer      | TRANSFER | Transfers a human being (chosen by humanId) to a different team (chosen by teamId). This changes the teamId field in the HumanBeing entity as well   | long, long                     | | Remove        | REMOVE   | Adds a human being (chosen by humanId) from a team (NOT chosen by teamId)                                                                            | long, null                     |
         *
         * @tags Teams
         * @name ManipulateHumanBeingToTeam
         * @summary Assign, transfer or delete a human being from/to a team.
         * @request PATCH:/teams
         */
        manipulateHumanBeingToTeam: (
            data: TeamPatchSchema,
            params: RequestParams = {},
        ) =>
            this.request<any[], any>({
                path: `/teams`,
                method: "PATCH",
                body: data,
                type: ContentType.Json,
                format: "json",
                ...params,
            }),

        /**
         * @description Get a singular team via their ID.
         *
         * @tags Teams
         * @name GetTeam
         * @summary Get existing team by given ID.
         * @request GET:/teams/{id}
         */
        getTeam: (id: number, params: RequestParams = {}) =>
            this.request<TeamFullSchema, void>({
                path: `/teams/${id}`,
                method: "GET",
                format: "json",
                ...params,
            }),

        /**
         * @description **Update** a specific team's fields by their ID.
         *
         * @tags Teams
         * @name UpdateTeam
         * @summary Update an existing team by their ID.
         * @request PUT:/teams/{id}
         */
        updateTeam: (id: number, data: TeamDTOSchema, params: RequestParams = {}) =>
            this.request<TeamFullSchema, void>({
                path: `/teams/${id}`,
                method: "PUT",
                body: data,
                type: ContentType.Json,
                format: "json",
                ...params,
            }),

        /**
         * @description **Deletes** a team (and their associated objects) by their given ID. If a team contains members at the moment of deletion, then the human beings will have their _teamId_'s set to null before proceeding with the deletion.
         *
         * @tags Teams
         * @name DeleteHumanBeing
         * @summary Deletes a team by their ID.
         * @request DELETE:/teams/{id}
         */
        deleteHumanBeing: (id: number, params: RequestParams = {}) =>
            this.request<void, void>({
                path: `/teams/${id}`,
                method: "DELETE",
                ...params,
            }),
    };
    search = {
        /**
         * @description Returns an array of all heroes or only **real** ones (realHero == true). This service calls the upstream Human Being service (/population/human-beings) and returns the corresponding set.
         *
         * @tags Heroes
         * @name HeroesSearch
         * @summary Find heroes (all or only real ones).
         * @request GET:/search/{real-hero-only}
         */
        heroesSearch: (realHeroOnly: boolean, params: RequestParams = {}) =>
            this.request<any[], any>({
                path: `/search/${realHeroOnly}`,
                method: "GET",
                format: "json",
                ...params,
            }),
    };
    team = {
        /**
         * @description For a team with id = {team-id}, **assign a red Lada Kalina** to all heroes who do not have a car (car == null). The operation calls the Human Being service and updates the corresponding objects.
         *
         * @tags Heroes
         * @name HeroesTeamCarAdd
         * @summary Assign red "Lada Kalina" to team members without cars.
         * @request PATCH:/team/{team-id}/car/add
         */
        heroesTeamCarAdd: (teamId: number, params: RequestParams = {}) =>
            this.request<any[], any>({
                path: `/team/${teamId}/car/add`,
                method: "PATCH",
                format: "json",
                ...params,
            }),
    };
}
