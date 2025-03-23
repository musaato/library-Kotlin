--
-- PostgreSQL database dump
--

-- Dumped from database version 16.6
-- Dumped by pg_dump version 16.6

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: check_publication_status_change(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.check_publication_status_change() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    IF OLD.publication_status = 'published' AND NEW.publication_status = 'unpublished' THEN
        RAISE EXCEPTION 'Cannot change publication_status from published to unpublished';
    END IF;
    RETURN NEW;
END;
$$;


ALTER FUNCTION public.check_publication_status_change() OWNER TO postgres;

--
-- Name: author_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.author_id_seq
    START WITH 1000001
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.author_id_seq OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: author; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.author (
    id bigint DEFAULT nextval('public.author_id_seq'::regclass) NOT NULL,
    first_name character varying(128) NOT NULL,
    last_name character varying(128) NOT NULL,
    profile_picture character varying(128) DEFAULT NULL::character varying,
    biography character varying(2048) DEFAULT NULL::character varying,
    nationality_code character varying(3) DEFAULT NULL::character varying,
    birthdate timestamp without time zone,
    created_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT author_birthdate_check CHECK ((birthdate < CURRENT_TIMESTAMP))
);


ALTER TABLE public.author OWNER TO postgres;

--
-- Name: book_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.book_id_seq
    START WITH 1000000
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.book_id_seq OWNER TO postgres;

--
-- Name: book; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.book (
    id bigint DEFAULT nextval('public.book_id_seq'::regclass) NOT NULL,
    isbn character varying(13) NOT NULL,
    title character varying(128) NOT NULL,
    price numeric(10,2) NOT NULL,
    publication_status character varying(11) DEFAULT 'published'::character varying NOT NULL,
    book_cover character varying(128) DEFAULT NULL::character varying,
    description character varying(2048) DEFAULT NULL::character varying,
    publication_date timestamp without time zone,
    created_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT book_price_check CHECK ((price >= (0)::numeric)),
    CONSTRAINT book_publication_status_check CHECK (((publication_status)::text = ANY ((ARRAY['unpublished'::character varying, 'published'::character varying])::text[])))
);


ALTER TABLE public.book OWNER TO postgres;

--
-- Name: book_authors; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.book_authors (
    book_id bigint NOT NULL,
    author_id bigint NOT NULL,
    role character varying(50) DEFAULT NULL::character varying,
    contribution character varying(255) DEFAULT NULL::character varying,
    created_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.book_authors OWNER TO postgres;

--
-- Name: flyway_schema_history; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.flyway_schema_history (
    installed_rank integer NOT NULL,
    version character varying(50),
    description character varying(200) NOT NULL,
    type character varying(20) NOT NULL,
    script character varying(1000) NOT NULL,
    checksum integer,
    installed_by character varying(100) NOT NULL,
    installed_on timestamp without time zone DEFAULT now() NOT NULL,
    execution_time integer NOT NULL,
    success boolean NOT NULL
);


ALTER TABLE public.flyway_schema_history OWNER TO postgres;

--
-- Data for Name: author; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.author (id, first_name, last_name, profile_picture, biography, nationality_code, birthdate, created_time, updated_time) FROM stdin;
1000101	Alice	Johnson	\N	\N	\N	1978-03-05 00:00:00	2016-02-18 11:45:30	2021-11-05 07:19:35
1000151	Bob	Brown	\N	\N	\N	1990-04-10 00:00:00	2017-08-23 13:10:40	2022-04-22 10:39:20
1000201	Charlie	Davis	\N	\N	\N	1982-05-25 00:00:00	2013-03-12 15:55:50	2018-09-16 12:26:40
1000251	Dana	Miller	\N	\N	\N	1988-06-30 00:00:00	2015-07-05 18:20:10	2020-12-01 09:53:55
1000301	Eve	Wilson	\N	\N	\N	1975-07-15 00:00:00	2011-11-22 08:35:15	2019-06-27 14:41:25
1000351	Frank	Taylor	\N	\N	\N	1983-08-20 00:00:00	2014-05-13 20:50:25	2021-02-09 17:05:50
1000401	Grace	Anderson	\N	\N	\N	1979-09-10 00:00:00	2018-01-09 22:15:35	2023-04-19 06:28:30
1000451	Hank	Thomas	\N	\N	\N	1984-10-05 00:00:00	2016-10-29 11:30:45	2022-08-15 05:59:40
1000501	Keith	Redick	\N	\N	\N	\N	2024-12-15 20:01:10.999364	2024-12-15 20:01:10.999364
1000551	Kimber	Alice	\N	\N	\N	\N	2024-12-15 20:01:10.999364	2024-12-15 20:01:10.999364
1000601	Keith02	Redick02	\N	\N	\N	\N	2024-12-16 21:09:23.974534	2024-12-16 21:09:23.974534
1000651	Kimber02	Alice02	\N	\N	\N	\N	2024-12-16 21:09:23.974534	2024-12-16 21:09:23.974534
1000701	Keith03	Redick03	\N	\N	\N	\N	2024-12-22 21:00:06.054018	2024-12-22 21:00:06.054018
1000751	Kimber03	Alice03	\N	\N	\N	\N	2024-12-22 21:00:06.054018	2024-12-22 21:00:06.054018
1000801	Nigel01	Coling01	https://example.com/profile01.jpg	Nigel Coling is a renowned author known for his captivating novels and insightful essays.	US	1989-06-04 00:00:00	2024-12-22 21:47:36.334536	2024-12-22 21:47:36.334536
1000851	Nigel03	Coling03	https://example.com/profile03.jpg	Nigel Coling03 is a renowned author known for his captivating novels and insightful essays.	UK	1999-06-04 00:00:00	2024-12-22 21:49:56.676909	2024-12-22 22:53:00.810188
1000001	John01	Doe01	\N	\N	\N	1980-01-15 00:00:00	2014-06-15 14:25:10	2025-02-09 13:26:49.144513
1000051	Jane01	Smith01	\N	\N	\N	1985-02-20 00:00:00	2012-09-10 09:30:20	2025-02-09 13:26:49.144513
\.


--
-- Data for Name: book; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.book (id, isbn, title, price, publication_status, book_cover, description, publication_date, created_time, updated_time) FROM stdin;
1000500	9781234567890	The Great Adventure	29.99	published	http://example.com/cover1.jpg	An exciting journey of discovery.	2023-01-15 10:00:00	2023-12-04 21:30:00	2023-12-04 21:30:00
1000550	9781234567891	Mystery of the Old House	19.99	unpublished	http://example.com/cover2.jpg	A gripping mystery novel.	2023-03-22 14:30:00	2023-12-04 21:30:00	2023-12-04 21:30:00
1000600	9781234567892	Science for Beginners	39.99	published	http://example.com/cover3.jpg	An introductory guide to science.	2024-06-10 09:45:00	2023-12-04 21:30:00	2023-12-04 21:30:00
1000650	9781234567893	History Unveiled	24.99	published	http://example.com/cover4.jpg	Exploring historical events.	2023-09-30 11:20:00	2023-12-04 21:30:00	2023-12-04 21:30:00
1000700	9781234567894	Cooking with Passion	34.99	unpublished	http://example.com/cover5.jpg	Delicious recipes from around the world.	2024-02-14 16:15:00	2023-12-04 21:30:00	2023-12-04 21:30:00
1000750	9781234567895	Tech Innovations	44.99	published	http://example.com/cover6.jpg	Latest advancements in technology.	2023-07-04 10:50:00	2023-12-04 21:30:00	2023-12-04 21:30:00
1000800	9781234567896	Healthy Living	22.99	published	http://example.com/cover7.jpg	Tips for a healthier lifestyle.	2023-05-25 13:35:00	2023-12-04 21:30:00	2023-12-04 21:30:00
1000850	9781234567897	Fantasy Worlds	18.99	published	http://example.com/cover8.jpg	Enter the realm of fantasy.	2023-11-11 08:20:00	2023-12-04 21:30:00	2023-12-04 21:30:00
1000900	9781234567898	Art and Creativity	27.99	unpublished	http://example.com/cover9.jpg	Unleash your artistic potential.	2023-08-17 15:45:00	2023-12-04 21:30:00	2023-12-04 21:30:00
1000950	9781234567899	Travel the World	49.99	published	http://example.com/cover10.jpg	A travel guide to the best destinations.	2024-01-01 12:00:00	2023-12-04 21:30:00	2023-12-04 21:30:00
1001050	9781234568001	Mystery of the Old House01	30.77	published	http://example.com/cover01.jpg	A gripping mystery novel01.	2024-03-12 14:30:00	2024-12-15 20:01:10.999364	2024-12-15 20:01:10.999364
1001100	9781234568002	Mystery of the Old House02	30.99	published	http://example.com/cover02.jpg	A gripping mystery novel02.	2024-04-12 05:30:00	2024-12-16 21:09:23.974534	2024-12-16 21:09:23.974534
1001150	9781234568003	Mystery of the Old House03	30.99	published	http://example.com/cover03.jpg	\N	\N	2024-12-22 21:00:06.054018	2024-12-22 21:00:06.054018
1001000	9781234568000	Mystery of the Old House_u01	21.99	published	http://example.com/cover2_u01.jpg	A gripping mystery novel_u01.	2023-03-16 05:30:00	2024-12-15 18:25:16.294142	2025-02-09 13:26:49.144513
1001250	9781234568004	Mystery of the Old House04	34.99	published	http://example.com/cover04.jpg	A gripping mystery novel04.	2024-04-13 05:30:00	2025-02-09 13:16:05.601201	2025-02-09 13:16:05.601201
1001300	9781234568005	Mystery of the Old House05	35.99	published	http://example.com/cover05.jpg	A gripping mystery novel05.	2024-04-15 05:30:00	2024-05-15 05:30:00	2024-05-15 05:30:00
\.


--
-- Data for Name: book_authors; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.book_authors (book_id, author_id, role, contribution, created_time, updated_time) FROM stdin;
1000500	1000001	Main Author	Wrote the entire book	2014-06-15 14:25:10	2020-03-11 08:12:45
1000550	1000001	Main Author	Wrote the entire book	2012-09-10 09:30:20	2019-08-23 16:47:50
1000600	1000051	Main Author	Wrote the entire book	2016-02-18 11:45:30	2021-11-05 07:19:35
1000650	1000051	Main Author	Wrote the entire book	2017-08-23 13:10:40	2022-04-22 10:39:20
1000700	1000101	Main Author	Wrote the entire book	2013-03-12 15:55:50	2018-09-16 12:26:40
1000750	1000101	Main Author	Wrote the entire book	2015-07-05 18:20:10	2020-12-01 09:53:55
1000800	1000151	Main Author	Wrote the entire book	2011-11-22 08:35:15	2019-06-27 14:41:25
1000850	1000151	Main Author	Wrote the entire book	2014-05-13 20:50:25	2021-02-09 17:05:50
1000900	1000201	Support Author	Wrote the partial book	2016-10-29 11:30:45	2022-08-15 05:59:40
1000900	1000151	Main Author	Wrote the majority book	2018-01-09 22:15:35	2023-04-19 06:28:30
1001050	1000501	Main	Drafted this book	2024-12-15 20:01:10.999364	2024-12-15 20:01:10.999364
1001050	1000551	Support	completed book	2024-12-15 20:01:10.999364	2024-12-15 20:01:10.999364
1000950	1000151	Main Author	Wrote the entire book	2021-06-16 15:42:32	2021-06-16 15:42:32
1001100	1000601	Main02	Drafted this book02	2024-12-16 21:09:23.974534	2024-12-16 21:09:23.974534
1001100	1000651	Support02	completed book02	2024-12-16 21:09:23.974534	2024-12-16 21:09:23.974534
1001150	1000701	Main03	Drafted this book03	2024-12-22 21:00:06.054018	2024-12-22 21:00:06.054018
1001150	1000751	Support03	completed book03	2024-12-22 21:00:06.054018	2024-12-22 21:00:06.054018
1001250	1000701	Main03	Drafted this book03	2025-02-09 13:16:05.601201	2025-02-09 13:16:05.601201
1001250	1000751	Support03	completed book03	2025-02-09 13:16:05.601201	2025-02-09 13:16:05.601201
1001000	1000001	Main writer_u01	Write almost the whole book_u01	2024-12-15 18:25:16.294142	2025-02-09 13:26:49.144513
1001000	1000051	Support writer_u01	Finished the book_u01	2024-12-15 18:25:16.294142	2025-02-09 13:26:49.144513
\.


--
-- Data for Name: flyway_schema_history; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) FROM stdin;
1	1	Baseline for existing schema	BASELINE	Baseline for existing schema	\N	postgres	2025-02-10 02:35:14.113649	0	t
2	2	flywayGenerationTest	SQL	V2__flywayGenerationTest.sql	-971029278	postgres	2025-02-10 02:37:20.486659	7	t
\.


--
-- Name: author_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.author_id_seq', 1000851, true);


--
-- Name: book_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.book_id_seq', 1001300, true);


--
-- Name: author author_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.author
    ADD CONSTRAINT author_pkey PRIMARY KEY (id);


--
-- Name: book_authors book_authors_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.book_authors
    ADD CONSTRAINT book_authors_pkey PRIMARY KEY (book_id, author_id);


--
-- Name: book book_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.book
    ADD CONSTRAINT book_pkey PRIMARY KEY (id);


--
-- Name: flyway_schema_history flyway_schema_history_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.flyway_schema_history
    ADD CONSTRAINT flyway_schema_history_pk PRIMARY KEY (installed_rank);


--
-- Name: flyway_schema_history_s_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX flyway_schema_history_s_idx ON public.flyway_schema_history USING btree (success);


--
-- Name: book_authors book_authors_author_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.book_authors
    ADD CONSTRAINT book_authors_author_id_fkey FOREIGN KEY (author_id) REFERENCES public.author(id);


--
-- Name: book_authors book_authors_book_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.book_authors
    ADD CONSTRAINT book_authors_book_id_fkey FOREIGN KEY (book_id) REFERENCES public.book(id);


--
-- PostgreSQL database dump complete
--

