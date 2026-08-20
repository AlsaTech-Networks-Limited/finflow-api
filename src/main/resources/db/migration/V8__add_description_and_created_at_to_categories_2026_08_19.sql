-- shared/domain/entity/Category.kt has always declared `description` and
-- `createdAt` fields, but V1__init.sql's categories table never had those
-- columns — R2dbcEntityTemplate's generated INSERT includes them regardless,
-- so every category create has failed with "bad SQL grammar" until now.
ALTER TABLE exp_finlow.categories
    ADD COLUMN description VARCHAR(1000),
    ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT NOW();
