// package org.tkit.onecx.bookmark.rs.internal.log;

// import java.util.List;

// import jakarta.enterprise.context.ApplicationScoped;

// import org.tkit.quarkus.log.cdi.LogParam;

// import gen.org.tkit.onecx.bookmark.rs.internal.model.BookmarkSearchCriteriaDTO;
// import gen.org.tkit.onecx.bookmark.rs.internal.model.CreateBookmarkDTO;
// import gen.org.tkit.onecx.bookmark.rs.internal.model.UpdateBookmarkDTO;

// @ApplicationScoped
// public class InternalLogParam implements LogParam {

//     @Override
//     public List<Item> getClasses() {
//         return List.of(
//                 item(10, CreateBookmarkDTO.class, x -> {
//                     CreateBookmarkDTO d = (CreateBookmarkDTO) x;
//                     return CreateBookmarkDTO.class.getSimpleName() + "[" + d.getProductName() + "," + d.getItemId() + "]";
//                 }),
//                 item(10, UpdateBookmarkDTO.class, x -> {
//                     UpdateBookmarkDTO d = (UpdateBookmarkDTO) x;
//                     return UpdateBookmarkDTO.class.getSimpleName() + "[" + d.getProductName() + "," + d.getItemId() + "]";
//                 }),
//                 item(10, BookmarkSearchCriteriaDTO.class, x -> {
//                     BookmarkSearchCriteriaDTO d = (BookmarkSearchCriteriaDTO) x;
//                     return BookmarkSearchCriteriaDTO.class.getSimpleName() + "[" + d.getPageNumber() + ","
//                             + d.getPageSize()
//                             + "]";
//                 }));
//     }
// }
