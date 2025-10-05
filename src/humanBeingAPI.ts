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

export enum Color {
  RED = "RED",
  BLUE = "BLUE",
  YELLOW = "YELLOW",
  GREEN = "GREEN",
  BLACK = "BLACK",
  WHITE = "WHITE",
}

export enum Mood {
  SADNESS = "SADNESS",
  SORROW = "SORROW",
  GLOOM = "GLOOM",
  APATHY = "APATHY",
  RAGE = "RAGE",
}

export enum WeaponType {
  AXE = "AXE",
  SHOTGUN = "SHOTGUN",
  MACHINE_GUN = "MACHINE_GUN",
}

export interface HumanBeingDTOSchema {
  /** @minLength 1 */
  name?: string;
  coordinates?: Coordinates;
  realHero?: boolean;
  hasToothpick?: boolean;
  /**
   * @format int32
   * @max 58
   */
  impactSpeed?: number;
  weaponType?: WeaponType | null;
  team?: null;
  mood?: Mood;
  car?: Car | null;
}

export type HumanBeingFullSchema = {
  /**
   * ID of a human being
   * @format int64
   * @exclusiveMin 0
   */
  id: number;
  /**
   * Creation date of a human being
   * @format date-time
   */
  creationDate?: string;
} & HumanBeingDTOSchema;

export interface HumanBeingPaginatedSchema {
  humanBeingGetResponseDtos?: HumanBeingFullSchema[];
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

export interface ImpactSpeedPaginatedSchema {
  uniqueSpeeds?: number[];
}

export interface Coordinates {
  /**
   * @format int32
   * @exclusiveMin -63
   */
  x?: number;
  /** @format double */
  y?: number;
}

export interface Car {
  cool?: null | boolean;
  color?: Color;
  model?: null | string;
}

export interface Error {
  /** @format int32 */
  code: number;
  message: string;
  /** @format date-time */
  time: string;
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
  public baseUrl: string = "https://localhost:15478/api/v1";
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
 * @title Human Being service REST endpoints
 * @version 1.0.2
 * @baseUrl https://localhost:15478/api/v1
 *
 * This is the first part of our assignment in our course 'Service-Oriented Architecture' where we generate Swagger documentation before implementing it in practice.
 */
export class Api<
  SecurityDataType extends unknown,
> extends HttpClient<SecurityDataType> {
  humanBeings = {
    /**
     * @description **Get all human beings** in the collection in the form of a _list_.
     *
     * @tags Human beings
     * @name GetHumanBeings
     * @summary Get list of human beings.
     * @request GET:/human-beings
     */
    getHumanBeings: (
      query?: {
        /**
         * Array of fields used for **sorting**. "-" is used for sorting in **descending** order. _If queries and deletion request are provided for one of these fields, independent of "-", an error will be returned._
         * @default "id"
         * @example ["-creationDate","name"]
         */
        sort?:
          | "id"
          | "name"
          | "coordinates.x"
          | "coordinates.y"
          | "creationDate"
          | "realHero"
          | "hasToothpick"
          | "impactSpeed"
          | "weaponType"
          | "mood"
          | "car.color"
          | "car.model"
          | "car.cool"
          | "-id"
          | "-name"
          | "-coordinates.x"
          | "-coordinates.y"
          | "-creationDate"
          | "-realHero"
          | "-hasToothpick"
          | "-impactSpeed"
          | "-weaponType"
          | "-mood"
          | "-car.color"
          | "-car.model"
          | "-car.cool";
        /**
         * Array of fields used for **filtering**. Each element must contain the name of the filterable field and the filtering method in square parentheses, as well as the value that will be used for filtering. An _invalid_ value will result in an **error response**. Below is a list of available operations and their code values, their descriptions and available datatypes for said operations.
         * ![No fucking way](https://64.media.tumblr.com/4b218f9ce82898062e6b56c88d160a18/49cb9142a33ad8da-30/s540x810/8cb2e0f908fa078eb8d37aec836d9c9ed066a661.gifv)
         * | **Operation**         | **Code** | **Description**                                                                                                               | **Datatype**                                                   |
         * |-----------------------|----------|-------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------|
         * | Equal                 | eq       | Filters objects by their chosen field and returns objects which have a value equal to the provided                            | String, int, float, double, long, boolean, enum, ZonedDateTime |
         * | Not equal             | neq      | Filters objects by their chosen field and returns objects which have a value not equal to the provided                        | String, int, float, double, long, boolean, enum, ZonedDateTime |
         * | Greater than          | gt       | Filters objects by their chosen field and returns objects which have a value greater than the provided                        | int, float, double, long, boolean, enum, ZonedDateTime         |
         * | Lesser than           | lt       | Filters objects by their chosen field and returns objects which have a value lesser than the provided                         | int, float, double, long, boolean, enum, ZonedDateTime         |
         * | Greater than or equal | gte      | Filters objects by their chosen field and returns objects which have a value greater than or equal to the provided            | int, float, double, long, enum, ZonedDateTime               |
         * | Lesser than or equal  | lte      | Filters objects by their chosen field and returns objects which have a value lesser than or equal to the provided             | int, float, double, long, enum, ZonedDateTime                  |
         * | Substring search      | like     | Filters objects by their chosen field and returns objects which have a value matched using substring search with the provided | String, int, float, double, long, boolean, enum, ZonedDateTime |
         * @example ["name[eq]=Sean%20Combs","realHero[eq]=true","weaponType[eq]=null","car.cool[neq]=false","mood[neq]=GLOOM","creationDate[gt]=2027-12-06T13:35:27.265Z","impactSpeed[lt]=53","coordinates.x[gte]=-5","coordinates.y[lte]=3.41","impactSpeed[like]=2"]
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
      this.request<HumanBeingPaginatedSchema, Error>({
        path: `/human-beings`,
        method: "GET",
        query: query,
        format: "json",
        ...params,
      }),

    /**
     * @description **Add** a new human being with valid fields.
     *
     * @tags Human beings
     * @name AddHumanBeing
     * @summary Add a new human being.
     * @request POST:/human-beings
     */
    addHumanBeing: (data: HumanBeingDTOSchema, params: RequestParams = {}) =>
      this.request<HumanBeingFullSchema, Error>({
        path: `/human-beings`,
        method: "POST",
        body: data,
        type: ContentType.Json,
        format: "json",
        ...params,
      }),

    /**
     * @description Get **a singular human being** via their ID.
     *
     * @tags Human beings
     * @name GetHumanBeing
     * @summary Get existing human being by given ID.
     * @request GET:/human-beings/{id}
     */
    getHumanBeing: (id: number, params: RequestParams = {}) =>
      this.request<HumanBeingFullSchema, Error>({
        path: `/human-beings/${id}`,
        method: "GET",
        format: "json",
        ...params,
      }),

    /**
     * @description Update a specific human being's fields by their ID.
     *
     * @tags Human beings
     * @name UpdateHumanBeing
     * @summary Update an existing human being by their ID.
     * @request PUT:/human-beings/{id}
     */
    updateHumanBeing: (
      id: number,
      data: HumanBeingDTOSchema,
      params: RequestParams = {},
    ) =>
      this.request<HumanBeingFullSchema, Error>({
        path: `/human-beings/${id}`,
        method: "PUT",
        body: data,
        type: ContentType.Json,
        format: "json",
        ...params,
      }),

    /**
     * @description **Deletes a human being** (and their associated objects) by their given ID.
     *
     * @tags Human beings
     * @name DeleteHumanBeing
     * @summary Deletes a human being by their ID.
     * @request DELETE:/human-beings/{id}
     */
    deleteHumanBeing: (id: number, params: RequestParams = {}) =>
      this.request<void, Error>({
        path: `/human-beings/${id}`,
        method: "DELETE",
        ...params,
      }),

    /**
     * @description Returns an array of **unique** integers which belong to human beings in the collection.
     *
     * @tags Human beings
     * @name GetUniqueImpactSpeeds
     * @summary Get an array of unique impactSpeed values in the collection.
     * @request GET:/human-beings/unique-speeds
     */
    getUniqueImpactSpeeds: (params: RequestParams = {}) =>
      this.request<ImpactSpeedPaginatedSchema, Error>({
        path: `/human-beings/unique-speeds`,
        method: "GET",
        format: "json",
        ...params,
      }),
  };
}
