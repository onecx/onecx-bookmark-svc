package org.tkit.onecx.bookmark.rs.exim.v1.log;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

import org.tkit.quarkus.log.cdi.LogParam;

import gen.org.tkit.onecx.bookmark.rs.exim.v1.model.ExportBookmarksRequestDTOV1;

@ApplicationScoped
public class EximLogParam implements LogParam {

    @Override
    public List<Item> getClasses() {
        return List.of(
                item(10, ExportBookmarksRequestDTOV1.class, x -> {
                    ExportBookmarksRequestDTOV1 d = (ExportBookmarksRequestDTOV1) x;
                    return ExportBookmarksRequestDTOV1.class.getSimpleName() + "[" + d.getWorkspaceName() + ","
                            + d.getScopes().toString() + "]";
                }));
    }
}
