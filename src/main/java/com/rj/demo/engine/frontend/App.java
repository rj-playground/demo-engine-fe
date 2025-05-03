package com.rj.demo.engine.frontend;

import org.apache.calcite.config.Lex;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.impl.AbstractTable;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql2rel.SqlToRelConverter;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.RelConversionException;
import org.apache.calcite.tools.ValidationException;

import io.substrait.isthmus.SqlToSubstrait;
import io.substrait.isthmus.SubstraitRelNodeConverter;

public class App {
    public static void main(String[] args) throws SqlParseException, ValidationException, RelConversionException {
        var rootSchema = Frameworks.createRootSchema(true);

        rootSchema.add("TEST_TABLE", new AbstractTable() {

            @Override
            public RelDataType getRowType(RelDataTypeFactory typeFactory) {
                return typeFactory.builder()
                        .add("id", typeFactory.createSqlType(org.apache.calcite.sql.type.SqlTypeName.INTEGER))
                        .add("name", typeFactory.createSqlType(org.apache.calcite.sql.type.SqlTypeName.VARCHAR))
                        .build();
            }
        });

        var config = Frameworks.newConfigBuilder()
                .defaultSchema(rootSchema)
                .parserConfig(SqlParser.Config.DEFAULT.withLex(Lex.BIG_QUERY))
                .build();
        
        var planner = Frameworks.getPlanner(config);
        var parsed = planner.parse("SELECT name FROM TEST_TABLE WHERE id > 10");
        var validated = planner.validate(parsed);

        var ast = planner.rel(validated).rel;

        System.out.println("Relational plan:\n" + RelOptUtil.toString(ast));
        
        var converter = new SqlToSubstrait();
        var substrait = converter.execute("SELECT name FROM TEST_TABLE WHERE id > 10", "", rootSchema);

        System.out.println("Relational plan:\n" + substrait.toString());
    }
}