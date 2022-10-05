package com.team1.dodam.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCertificationNumber is a Querydsl query type for CertificationNumber
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCertificationNumber extends EntityPathBase<CertificationNumber> {

    private static final long serialVersionUID = 541112073L;

    public static final QCertificationNumber certificationNumber1 = new QCertificationNumber("certificationNumber1");

    public final NumberPath<Long> certificationNumber = createNumber("certificationNumber", Long.class);

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QCertificationNumber(String variable) {
        super(CertificationNumber.class, forVariable(variable));
    }

    public QCertificationNumber(Path<? extends CertificationNumber> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCertificationNumber(PathMetadata metadata) {
        super(CertificationNumber.class, metadata);
    }

}

